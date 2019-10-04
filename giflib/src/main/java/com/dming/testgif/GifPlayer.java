package com.dming.testgif;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

public class GifPlayer {

    public enum PlayState {
        IDLE, PREPARE, PLAYING, STOP
    }

    private int mTexture;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private PlayState mPlayState;
    private long mGifPlayerPtr = 0;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private int mSTexture;
    private EglHelper mEglHelper;
    private GifFilter mGifFilter;
    private int mWidth, mHeight;

    public GifPlayer(final Context context) {
        mEglHelper = new EglHelper();
        mGifPlayerPtr = native_create();
        mPlayState = PlayState.IDLE;
        mHandlerThread = new HandlerThread("GifPlayer");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mSTexture = FGLUtils.createTexture();
        mSurfaceTexture = new SurfaceTexture(mSTexture);
        mSurface = new Surface(mSurfaceTexture);
        final EGL10 mEgl = (EGL10) EGLContext.getEGL();
        mHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                mEglHelper.initEgl(mEgl.eglGetCurrentContext(), mSurface);
                mEglHelper.glBindThread();
                mGifFilter = new GifFilter(context);
                GLES20.glClearColor(1, 1, 1, 1);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }
        });
    }

    public void surfaceChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void surfaceDestroyed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mGifFilter.onDestroy();
                mEglHelper.destroyEgl();
                mSurfaceTexture.release();
                mSurface.release();
            }
        });
    }

    public boolean assetPlay(Context context, String gifPath) {
        return play(false, context, gifPath);
    }

    public boolean assetPlay(boolean once, Context context, String gifPath) {
        return play(once, context, gifPath);
    }

    private boolean play(final boolean once, final Context context, final String gifPath) {
        if (mPlayState == PlayState.IDLE && mSurface != null) {
            mPlayState = PlayState.PREPARE;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (native_load(mGifPlayerPtr, context != null ? context.getResources().getAssets() : null, gifPath)) {
                        mPlayState = PlayState.PLAYING;
                        mTexture = FGLUtils.createTexture();
                        native_start(mGifPlayerPtr, once, mTexture, new Runnable() {
                            @Override
                            public void run() {
                                mGifFilter.onDraw(mTexture, 0, 0, mWidth, mHeight);
                                FGLUtils.glCheckErr("test down");
                                mEglHelper.swapBuffers();
                            }
                        });
                    }
                    mPlayState = PlayState.IDLE;
                }
            });
        } else {
            return false;
        }
        return true;
    }

    public boolean pause() {
        if (mPlayState == PlayState.PLAYING) {
            native_pause(mGifPlayerPtr);
            return true;
        }
        return false;
    }

    public boolean resume() {
        if (mPlayState == PlayState.PLAYING) {
            native_resume(mGifPlayerPtr);
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (mPlayState != PlayState.IDLE) {
            mPlayState = PlayState.STOP;
            native_stop(mGifPlayerPtr);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPlayState = PlayState.IDLE;
                }
            });
            return true;
        }
        return false;
    }

    public void destroy() {
        native_stop(mGifPlayerPtr);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                native_release(mGifPlayerPtr);
                mGifPlayerPtr = 0;
                mHandler = null;
                mHandlerThread.quit();
                mHandlerThread = null;
            }
        });
    }

    static {
        System.loadLibrary("gifplayer");
    }

    private native long native_create();

    private native boolean native_load(long ptr, AssetManager assetManager, String gifPath);

    private native void native_start(long ptr, boolean once, int texture, Runnable updateBitmap);

    private native int native_get_width(long ptr);

    private native int native_get_height(long ptr);

    private native void native_pause(long ptr);

    private native void native_resume(long ptr);

    private native void native_stop(long ptr);

    private native void native_release(long ptr);

}
