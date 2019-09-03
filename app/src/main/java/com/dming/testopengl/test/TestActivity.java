package com.dming.testopengl.test;

import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.NoFilter;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.FGLUtils;

public class TestActivity extends AppCompatActivity {

    private SurfaceView mTestSv;
    private SurfaceView mTestSv2;
    private EglHelper mEglHelper = new EglHelper();
    private EglHelper mEglHelper2 = new EglHelper();
    private LineGraph mLineGraph;
    private NoFilter mNoFilter;
    private int mFrameBufferTexture = -1;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);
        mTestSv = findViewById(R.id.sv_test);
        mTestSv2 = findViewById(R.id.sv_test_2);

        mHandlerThread = new HandlerThread("gl2");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        findViewById(R.id.btn_test_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLog.i("mFrameBufferTexture draw: " + mFrameBufferTexture);
                if (mFrameBufferTexture != -1) {
                    GLES20.glClearColor(1,1,1,1);
                    mNoFilter.onDraw(mFrameBufferTexture, 0, 0, 200, 200);
                    mEglHelper.swapBuffers();
                    FGLUtils.glCheckErr();
                }
            }
        });
        mTestSv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                DLog.i("EglHelper surfaceCreated: " + Thread.currentThread().getName());
                mEglHelper.initEgl(null, holder.getSurface());
                FGLUtils.glCheckErr();
                mLineGraph = new LineGraph(TestActivity.this);
                mNoFilter = new NoFilter(TestActivity.this);
                testTwoThread();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                DLog.i("EglHelper surfaceChanged");
                mLineGraph.onChange(width, height, 0);
                mNoFilter.onChange(width, height, 0);
                GLES20.glClearColor(1, 1, 1, 1);
                mLineGraph.onDraw(0, 0, 0, width, height);
                mEglHelper.swapBuffers();
                int err = GLES20.glGetError();
                DLog.i("EglHelper surfaceChanged err: " + err);
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
                DLog.i("HandlerThread: " + Thread.currentThread().getName());
                mEglHelper2.initEgl(mEglHelper.getEglContext(), mTestSv2.getHolder().getSurface());
                FGLUtils.glCheckErr();
                LineGraph mLineGraph = new LineGraph(TestActivity.this);
                int[] ids = FGLUtils.createFBO(200, 200);
                DLog.i("ids>>>"+ids);
                if (ids != null) {
                    int frameBuffer = ids[0];
                    mFrameBufferTexture = ids[1];
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
                    mLineGraph.onDraw(0, 0, 0, 200, 200);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    FGLUtils.glCheckErr();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mHandlerThread != null){
            mHandlerThread.quit();
        }
        super.onDestroy();
    }
}
