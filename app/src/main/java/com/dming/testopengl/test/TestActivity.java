package com.dming.testopengl.test;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.NoFilter;
import com.dming.testopengl.filter.TestLineGraph;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.FGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TestActivity extends AppCompatActivity {

    private SurfaceView mTestSv;
    private ImageView mTestIv;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private EglHelper mEglHelper = new EglHelper();
    private EglHelper mEglHelper2 = new EglHelper();
    private LineGraph mLineGraph;
    private NormalFilter mNoFilter;
    private int mFrameBufferTexture = -1;
    //    private int mOESTexture = -1;
//    private int mTexture = -1;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);
        mTestSv = findViewById(R.id.sv_test);
        mTestIv = findViewById(R.id.iv_test);
        mHandlerThread = new HandlerThread("gl2");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        findViewById(R.id.btn_test_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLog.i("mFrameBufferTexture draw: " + mFrameBufferTexture);
//                if (mOESTexture != -1) {
//                    mSurfaceTexture.updateTexImage();
//                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//                    mNoFilter.onDraw(mOESTexture, 0, 0, 200, 200);
//                    mEglHelper.swapBuffers();
//                    FGLUtils.glCheckErr();
//                }
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                int w = 200;
                int h = 200;
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTexture);
                mNoFilter.onDraw(mFrameBufferTexture, 0, 0, w, h);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                FGLUtils.glCheckErr("mFrameBufferTexture");
                mEglHelper.swapBuffers();
            }
        });
        mTestSv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                DLog.i("EglHelper surfaceCreated: " + Thread.currentThread().getName());
                mEglHelper.initEgl(null, holder.getSurface());
//                mOESTexture = FGLUtils.createOESTexture();
                FGLUtils.glCheckErr();
                mLineGraph = new LineGraph(TestActivity.this);
                mNoFilter = new NormalFilter(TestActivity.this);
                testTwoThread();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                DLog.i("EglHelper surfaceChanged: ");
                mLineGraph.onChange(width, height, 0);
                mNoFilter.onChange(width, height, 0);
                GLES20.glClearColor(1, 1, 1, 1);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                mLineGraph.onDraw(0, 0, 0, width, height);
                FGLUtils.glCheckErr(11);

//                ByteBuffer readByte = ByteBuffer.allocateDirect(w * h * fmt);
//                GLES20.glReadPixels(0,0,w,h,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,readByte);
//                DLog.i("readByte 0: "+readByte.get(0) + " 1: " + readByte.get(1)+ " 2: " + readByte.get(2)+ " 3: " + readByte.get(3));
//                Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
//                bitmap.copyPixelsFromBuffer(readByte);
//                mTestIv.setImageBitmap(bitmap);
                mEglHelper.swapBuffers();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DLog.i("EglHelper surfaceDestroyed");
                mLineGraph.onDestroy();
                mEglHelper.destoryEgl();
            }
        });
    }

    public void testTwoThread() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int w = 200;
                int h = 200;
                int fmt = 4;
                mSurfaceTexture = new SurfaceTexture(0);
                mSurface = new Surface(mSurfaceTexture);
                mSurfaceTexture.setDefaultBufferSize(w, h);
                mEglHelper2.initEgl(mEglHelper.getEglContext(), mSurface);
                LineGraph mLineGraph = new LineGraph(TestActivity.this);
                mLineGraph.onChange(w, h, 0);
                int[] ids = FGLUtils.createFBO(w, h);
                if (ids != null) {
                    int frameBuffer = ids[0];
                    mFrameBufferTexture = ids[1];
                    GLES20.glViewport(0, 0, w, h);
                    GLES20.glClearColor(0, 0, 1, 1);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    FGLUtils.glCheckErr("ByteBuffer>");
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(w * h * fmt);
                    byteBuffer.order(ByteOrder.nativeOrder());
                    for (int i = 0; i < w * h * fmt; i += fmt) {
                        byteBuffer.put((byte) 0x00);
                        byteBuffer.put((byte) 0x00);
                        byteBuffer.put((byte) 0xff);
                        byteBuffer.put((byte) 0xFF);
                    }
                    byteBuffer.position(0);
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTexture);
                    FGLUtils.glCheckErr(22);
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, w, h, 0,
                            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
                    mLineGraph.onDraw(mFrameBufferTexture, 0, 0, w, h);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                    FGLUtils.glCheckErr("mEglHelper2");
                    mEglHelper2.swapBuffers();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        if (mSurface != null) {
            mSurface.release();
        }
        super.onDestroy();
    }
}
