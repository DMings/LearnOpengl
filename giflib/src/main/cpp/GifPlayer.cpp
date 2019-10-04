#include <jni.h>
#include <string>
#include <malloc.h>
#include <string.h>
#include "log.h"
#include <unistd.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <fcntl.h>
#include "GifPlayer.h"
#include "GLUtils.h"

#define DATA_OFFSET 3

void GifPlayer::PrintGifError(int Error) {
    LOGI("PrintGifError: %s", GifErrorString(Error));
}

void GifPlayer::setPlayState(PlayState state) {
    pthread_mutex_lock(&play_mutex);
    play_state = state;
    pthread_mutex_unlock(&play_mutex);
}

void GifPlayer::getPlayState(PlayState *playState) {
    pthread_mutex_lock(&play_mutex);
    *playState = play_state;
    pthread_mutex_unlock(&play_mutex);
}

int fileRead(GifFileType *gif, GifByteType *buf, int size) {
    AAsset *asset = (AAsset *) gif->UserData;
    return AAsset_read(asset, buf, (size_t) size);
}

int GifPlayer::prepareGif(JNIEnv *env, jobject assetManager, const char *filename) {
    int Error;
    gif_width = 0;
    gif_height = 0;
    is_play_quit = false;
    threadSleep.reset();
    transparentColorIndex = NO_TRANSPARENT_COLOR;
    bgColorIndex = 0;
    if (gifFile != NULL) {
        DGifCloseFile(gifFile, &Error);
        gifFile = NULL;
    }
    setPlayState(PREPARE);
    if (assetManager) {
        AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
        AAsset *asset = AAssetManager_open(mgr, filename, AASSET_MODE_STREAMING);
        if ((gifFile = DGifOpen(asset, fileRead, &Error)) == NULL) {
            setPlayState(IDLE);
            PrintGifError(Error);
            return -1;
        }
    } else {
        if ((gifFile = DGifOpenFileName(filename, &Error)) == NULL) { // 用外部路径用这里
            PrintGifError(Error);
            return -1;
        }
    }
    gif_width = gifFile->SWidth;
    gif_height = gifFile->SHeight;
    LOGI("gif SWidth: %d SHeight: %d", gifFile->SWidth, gifFile->SHeight);
    return 0;
}

uint32_t GifPlayer::gifColorToColorARGB(const GifColorType &color) {
    return (uint32_t) (MAKE_COLOR_ABGR(color.Red, color.Green, color.Blue));
}

void GifPlayer::setColorARGB(uint32_t *sPixels, int imageIndex,
                             ColorMapObject *colorMap, GifByteType colorIndex) {
    if (imageIndex > 0 && disposalMode == DISPOSE_DO_NOT && colorIndex == transparentColorIndex) {
        return;
    }
    if (colorIndex != transparentColorIndex || transparentColorIndex == NO_TRANSPARENT_COLOR) {
        *sPixels = gifColorToColorARGB(colorMap->Colors[colorIndex]);
    } else {
        *sPixels = 0;
    }
}

// RGBA_8888
void GifPlayer::drawGL(uint texture, uint32_t *pixels, int imageIndex,
                       SavedImage *SavedImages, ColorMapObject *ColorMap,
                       GifRowType *ScreenBuffer,
                       int left, int top,
                       int width, int height) {

    int dataOffset = sizeof(int32_t) * DATA_OFFSET;
    int dH = gif_width * top;
    GifByteType colorIndex;
    for (int h = top; h < height; h++) {
        for (int w = left; w < width; w++) {
            colorIndex = (GifByteType) ScreenBuffer[h][w];
            setColorARGB(&pixels[dH + w],
                         imageIndex,
                         ColorMap,
                         colorIndex);
            if (SavedImages != NULL) {
                SavedImages->RasterBits[dataOffset++] = colorIndex;
            }
        }
        dH += gif_width;
    }

//    glClear(GL_COLOR_BUFFER_BIT);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, gif_width, gif_height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                 pixels);
    glBindTexture(GL_TEXTURE_2D, 0);
    GLUtils::checkErr("draw");
}

void GifPlayer::playGif(JNIEnv *env, bool once, uint texture, jobject runnable) {
    int i, j, Row, Col, Width, Height, ExtCode;
    GifRecordType RecordType;
    GifByteType *Extension;
    GifRowType *ScreenBuffer;
    int InterlacedOffset[] = {0, 4, 2, 1}; /* The way Interlaced image should. */
    int InterlacedJumps[] = {8, 8, 4, 2};    /* be read - offsets and jumps... */
    int ImageNum = 0;
    int Error;
    GifByteType *GifExtension;
    ColorMapObject *ColorMap;
    size_t size;
    SavedImage *sp = NULL;
    int32_t delayTime = 0;
    unsigned int dt = 0;
    int32_t *user_image_data;
    uint32_t *gl_data;
    //
    LOGI("texture: %d", texture);
    //
    setPlayState(PLAYING);

    jclass runClass = env->GetObjectClass(runnable);
    jmethodID runMethod = env->GetMethodID(runClass, "run", "()V");

    if ((gl_data = (uint32_t *)
            malloc(gif_width * gif_height * sizeof(uint32_t))) == NULL) {
        LOGI("Failed to allocate memory required, aborted1.");
        goto end;
    }

    if ((ScreenBuffer = (GifRowType *)
            malloc(gifFile->SHeight * sizeof(GifRowType))) == NULL) {
        LOGI("Failed to allocate memory required, aborted2.");
        goto end;
    }
    size = gifFile->SWidth * sizeof(GifPixelType);
    if ((ScreenBuffer[0] = (GifRowType) malloc(size)) == NULL) {
        LOGE("Failed to allocate memory required, aborted.");
        goto end;
    }
    GifPixelType *buffer;
    buffer = (GifPixelType *) (ScreenBuffer[0]);
    for (i = 0; i < gifFile->SWidth; i++)
        buffer[i] = (GifPixelType) gifFile->SBackGroundColor;
    for (i = 1; i < gifFile->SHeight; i++) {
        if ((ScreenBuffer[i] = (GifRowType) malloc(size)) == NULL) {
            LOGI("Failed to allocate memory required, aborted.");
            goto end;
        }
        memcpy(ScreenBuffer[i], ScreenBuffer[0], size);
    }
    syncTime.set_clock();
    do {
        if (DGifGetRecordType(gifFile, &RecordType) == GIF_ERROR) {
            PrintGifError(gifFile->Error);
            goto end;
        }
        switch (RecordType) {
            case IMAGE_DESC_RECORD_TYPE:
                if (DGifGetImageDesc(gifFile) == GIF_ERROR) {
                    PrintGifError(gifFile->Error);
                    goto end;
                }
                sp = &gifFile->SavedImages[gifFile->ImageCount - 1];
                sp->RasterBits = (unsigned char *) malloc(
                        sizeof(GifPixelType) * gif_width * gif_height +
                        sizeof(int32_t) * 2);
                user_image_data = (int32_t *) sp->RasterBits;
                user_image_data[0] = delayTime;
                user_image_data[1] = transparentColorIndex;
                user_image_data[2] = disposalMode;
//                LOGI(">>>time: %d tColorIndex: %d", p2[0], transparentColorIndex);
                //
                Row = gifFile->Image.Top;
                Col = gifFile->Image.Left;
                Width = gifFile->Image.Width;
                Height = gifFile->Image.Height;

//                LOGI("gifFile Image %d at (%d, %d) [%dx%d] tColorIndex:%d", ++ImageNum, Col, Row,
//                     Width, Height, transparentColorIndex);

                if (gifFile->Image.Left + gifFile->Image.Width > gifFile->SWidth ||
                    gifFile->Image.Top + gifFile->Image.Height > gifFile->SHeight) {
                    LOGI("Image %d is not confined to screen dimension, aborted", ImageNum);
                    goto end;
                }
                if (gifFile->Image.Interlace) {
                    for (i = 0; i < 4; i++)
                        for (j = Row + InterlacedOffset[i]; j < Row + Height;
                             j += InterlacedJumps[i]) {
                            if (DGifGetLine(gifFile, &ScreenBuffer[j][Col],
                                            Width) == GIF_ERROR) {
                                PrintGifError(gifFile->Error);
                                goto end;
                            }
                        }
                } else {
                    for (i = 0; i < Height; i++) {
                        if (DGifGetLine(gifFile, &ScreenBuffer[Row++][Col],
                                        Width) == GIF_ERROR) {
                            PrintGifError(gifFile->Error);
                            goto end;
                        }
                    }
                }
                ColorMap = (gifFile->Image.ColorMap
                            ? gifFile->Image.ColorMap
                            : gifFile->SColorMap);
                //
                dt = syncTime.synchronize_time(delayTime * 10);
                threadSleep.msleep(dt);
                delayTime = 0;
                //
                pthread_mutex_lock(&play_mutex);
                if (is_pause) {
                    is_pause = false;
                    pthread_cond_wait(&play_cond, &play_mutex);
                }
                pthread_mutex_unlock(&play_mutex);
                //
                drawGL(texture, gl_data, gifFile->ImageCount - 1,
                       sp, ColorMap, ScreenBuffer,
                       gifFile->Image.Left, gifFile->Image.Top,
                       gifFile->Image.Left + Width, gifFile->Image.Top + Height);
                env->CallVoidMethod(runnable, runMethod);
                syncTime.set_clock();
                break;
            case EXTENSION_RECORD_TYPE:
                if (DGifGetExtension(gifFile, &ExtCode, &Extension) == GIF_ERROR) {
                    PrintGifError(gifFile->Error);
                    goto end;
                }
                if (ExtCode == GRAPHICS_EXT_FUNC_CODE) {
                    if (Extension[0] != 4) {
                        PrintGifError(GIF_ERROR);
                        goto end;
                    }
                    GifExtension = Extension + 1;
                    delayTime = UNSIGNED_LITTLE_ENDIAN(GifExtension[1], GifExtension[2]);
                    if (delayTime < 1) { // 如果没有时间，写个默认6。。。
                        delayTime = 6;
                    }
                    if (GifExtension[0] & 0x01) {
                        transparentColorIndex = (int) GifExtension[3];
                    } else {
                        transparentColorIndex = NO_TRANSPARENT_COLOR;
                    }
                    disposalMode = (GifExtension[0] >> 2) & 0x07;
                }
                while (Extension != NULL) {
                    if (DGifGetExtensionNext(gifFile, &Extension) == GIF_ERROR) {
                        PrintGifError(gifFile->Error);
                        goto end;
                    }
                }
                break;
            case TERMINATE_RECORD_TYPE:
                break;
            default:
                break;
        }
    } while (RecordType != TERMINATE_RECORD_TYPE && !is_play_quit);

    // 释放不再使用变量
    for (i = 0; i < gifFile->SHeight; i++) {
        free(ScreenBuffer[i]);
    }
    free(ScreenBuffer);
    ScreenBuffer = NULL;
    if (gifFile->UserData) {
        AAsset *asset = (AAsset *) gifFile->UserData;
        AAsset_close(asset);
        gifFile->UserData = NULL;
    }
    //释放不再使用变量

    syncTime.set_clock();
    while (!is_play_quit && !once) {
        for (int t = 0; t < gifFile->ImageCount; t++) {
            if (is_play_quit) {
                break;
            }
            SavedImage frame = gifFile->SavedImages[t];
            GifImageDesc frameInfo = frame.ImageDesc;
//            LOGI("gifFile Image %d at (%d, %d) [%dx%d]", t, frameInfo.Left, frameInfo.Top,
//                 frameInfo.Width, frameInfo.Height);
            ColorMap = (frameInfo.ColorMap
                        ? frameInfo.ColorMap
                        : gifFile->SColorMap);
            //
            int32_t d_time = 0;
            user_image_data = (int32_t *) frame.RasterBits;
            d_time = user_image_data[0];
            transparentColorIndex = user_image_data[1];
            disposalMode = user_image_data[2];
            dt = syncTime.synchronize_time(d_time * 10);
            threadSleep.msleep(dt);
            //
            pthread_mutex_lock(&play_mutex);
            if (is_pause) {
                is_pause = false;
                pthread_cond_wait(&play_cond, &play_mutex);
            }
            pthread_mutex_unlock(&play_mutex);
            //
//           LOGI("d_time: %d tColorIndex: %d", d_time, tColorIndex);
            //
            int pointPixelIdx = sizeof(int32_t) * DATA_OFFSET;
            int dH = gif_width * frameInfo.Top;
            for (int h = frameInfo.Top; h < frameInfo.Top + frameInfo.Height; h++) {
                for (int w = frameInfo.Left; w < frameInfo.Left + frameInfo.Width; w++) {
                    setColorARGB(&gl_data[dH + w],
                                 t,
                                 ColorMap,
                                 frame.RasterBits[pointPixelIdx++]);
                }
                dH += gif_width;
            }
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, gif_width, gif_height, 0, GL_RGBA,
                         GL_UNSIGNED_BYTE,
                         gl_data);
            glBindTexture(GL_TEXTURE_2D, 0);
            //
            env->CallVoidMethod(runnable, runMethod);
            //
            syncTime.set_clock();
        }
    }
    // free
    end:
    if (ScreenBuffer) {
        free(ScreenBuffer);
    }
    if (gifFile->UserData) {
        AAsset *asset = (AAsset *) gifFile->UserData;
        AAsset_close(asset);
        gifFile->UserData = NULL;
    }
    if (DGifCloseFile(gifFile, &Error) == GIF_ERROR) {
        PrintGifError(Error);
    }
    gifFile = NULL;
    setPlayState(IDLE);
}

/////////////////////////////////jni/////////////////////////////////////////////

jboolean GifPlayer::load_gif(JNIEnv *env, jobject assetManager, const char *gifPath) {
    PlayState playState;
    int ret;
    getPlayState(&playState);
    if (playState == IDLE) {
        ret = prepareGif(env, assetManager, gifPath);
    } else {
        ret = -1;
    }
    return (jboolean) (ret == 0 ? JNI_TRUE : JNI_FALSE);
}

void GifPlayer::start(JNIEnv *env, jboolean once,
                      jint texture, jobject runnable) {
    PlayState playState;
    getPlayState(&playState);
    if (playState == PREPARE) {
        playGif(env, once, (uint) texture, runnable);
    }
}

void GifPlayer::pause() {
    pthread_mutex_lock(&play_mutex);
    if (play_state == PLAYING) {
        is_pause = true;
    }
    pthread_mutex_unlock(&play_mutex);
}

void GifPlayer::resume() {
    pthread_mutex_lock(&play_mutex);
    is_pause = false;
    pthread_cond_signal(&play_cond);
    pthread_mutex_unlock(&play_mutex);
}

jint GifPlayer::get_width() {
    return gif_width;
}

jint GifPlayer::get_height() {
    return gif_height;
}

void GifPlayer::release() {
    pthread_mutex_lock(&play_mutex);
    is_play_quit = true;
    is_pause = false;
    pthread_cond_signal(&play_cond);
    pthread_mutex_unlock(&play_mutex);
    threadSleep.interrupt();
}
