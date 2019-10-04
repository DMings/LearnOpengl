//
// Created by Administrator on 2019/9/30.
//

#ifndef TESTGIF_GIFJNI_H
#define TESTGIF_GIFJNI_H

#include <jni.h>
#include "giflib/gif_lib.h"
#include "PthreadSleep.h"
#include "SyncTime.h"

#define UNSIGNED_LITTLE_ENDIAN(lo, hi)    ((lo) | ((hi) << 8))
#define  MAKE_COLOR_ABGR(r, g, b) ((0xff) << 24 ) | ((b) << 16 ) | ((g) << 8 ) | ((r) & 0xff)

enum PlayState {
    IDLE, PREPARE, PLAYING
};

class GifPlayer {
public:
    jboolean load_gif(JNIEnv *env, jobject assetManager, const char *gifPath);

    void start(JNIEnv *env, jboolean once, jint texture, jobject runnable);

    void pause();

    void resume();

    jint get_width();

    jint get_height();

    void release();

private:
    GifFileType *gifFile = NULL;
    PthreadSleep threadSleep;
    SyncTime syncTime;
    pthread_mutex_t play_mutex = PTHREAD_MUTEX_INITIALIZER;
    pthread_cond_t play_cond = PTHREAD_COND_INITIALIZER;
    bool is_pause = false;
    bool is_play_quit = false;
    int gif_width = 0;
    int gif_height = 0;
    int transparentColorIndex = 0;
    int bgColorIndex;
    int disposalMode = DISPOSAL_UNSPECIFIED;
    enum PlayState play_state = IDLE;

    void setPlayState(PlayState state);

    void getPlayState(PlayState *playState);

    void PrintGifError(int Error);

    int prepareGif(JNIEnv *env, jobject assetManager, const char *filename);

    uint32_t gifColorToColorARGB(const GifColorType &color);

    void setColorARGB(uint32_t *sPixels, int imageIndex, ColorMapObject *colorMap,
                      GifByteType colorIndex);

    void playGif(JNIEnv *env, bool once, uint texture, jobject runnable);

    void drawGL(uint texture, uint32_t *pixels, int imageIndex,
                SavedImage *SavedImages, ColorMapObject *ColorMap,
                GifRowType *ScreenBuffer,
                int left, int top,
                int width, int height);
};

#endif //TESTGIF_GIFJNI_H
