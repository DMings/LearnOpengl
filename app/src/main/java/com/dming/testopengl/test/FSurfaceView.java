package com.dming.testopengl.test;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLContext;

public class FSurfaceView extends SurfaceView {

    private EglHelper mEglHelper;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private GLRenderer mGLRenderer;

    private enum GLState {
        INIT, DESTROY, RUNNING, PAUSE
    }

    private GLState mGLState;

    public FSurfaceView(Context context) {
        super(context);
        init();
    }

    public FSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setGLRenderer(GLRenderer glRenderer) {
        mGLRenderer = glRenderer;
    }

    private void init() {
        mEglHelper = new EglHelper();
        mHandlerThread = new HandlerThread("fsv");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private void glInit(final Surface surface) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglHelper != null) {
                        mEglHelper.initEgl(null, surface);
                        mEglHelper.glBindThread();
                        mGLState = GLState.INIT;
                    }
                }
            });
        }
    }

    private void glRunning() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglHelper != null) {
                        if (mGLState == GLState.INIT || mGLState == GLState.PAUSE) {
                            mGLState = GLState.RUNNING;
                        }
                    }
                }
            });
        }
    }

    private void glPause() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglHelper != null) {
                        mGLState = GLState.PAUSE;
                    }
                }
            });
        }
    }

    private void glDestroy() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mEglHelper != null) {
                        mGLState = GLState.DESTROY;
                        mEglHelper.destroyEgl();
                    }
                }
            });
        }
    }

    public EGLContext getEglContext() {
        if (mEglHelper != null) {
            return mEglHelper.getEglContext();
        } else {
            return null;
        }
    }

    public void swapBuffers() {
        if (mEglHelper != null) {
            mEglHelper.swapBuffers();
        }
    }

    public void postDraw(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mGLState == GLState.RUNNING) {
                        runnable.run();
                        mEglHelper.swapBuffers();
                    }
                }
            });
        }
    }

    public void postEvent(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mGLState == GLState.RUNNING) {
                        runnable.run();
                    }
                }
            });
        }
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            DLog.i("surfaceCreated: ");
            glInit(holder.getSurface());
            glRunning();
            postEvent(new Runnable() {
                @Override
                public void run() {
                    if (mGLRenderer != null) {
                        mGLRenderer.surfaceCreated();
                    }
                }
            });

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
            DLog.i("surfaceChanged: ");
            postEvent(new Runnable() {
                @Override
                public void run() {
                    if (mGLRenderer != null) {
                        mGLRenderer.surfaceChanged(width, height);
                    }
                }
            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            DLog.i("surfaceDestroyed: ");
            postEvent(new Runnable() {
                @Override
                public void run() {
                    if (mGLRenderer != null) {
                        mGLRenderer.surfaceDestroyed();
                    }
                }
            });
            glDestroy();
        }
    };

    public void onResume() {
        getHolder().removeCallback(mCallback);
        getHolder().addCallback(mCallback);
        glRunning();
    }

    public void onPause() {
        getHolder().removeCallback(mCallback);
        glPause();
    }

    public void onDestroy() {
        getHolder().removeCallback(mCallback);
        glDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
        mHandler = null;
    }

    public interface GLRenderer {
        void surfaceCreated();

        void surfaceChanged(int width, int height);

        void surfaceDestroyed();
    }


}
