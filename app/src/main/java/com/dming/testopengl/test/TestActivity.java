package com.dming.testopengl.test;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.utils.DLog;

public class TestActivity extends AppCompatActivity {

    private SurfaceView mTestSv;
    private EglHelper mEglHelper = new EglHelper();
    private EglHelper mEglHelper2 = new EglHelper();
    private LineGraph mLineGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);
        mTestSv = findViewById(R.id.sv_test);
        findViewById(R.id.btn_test_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTestSv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                DLog.i("EglHelper surfaceCreated");
                mEglHelper.initEgl(holder.getSurface(),null);
                int err = GLES20.glGetError();
                DLog.i("EglHelper err: "+err);
                mEglHelper2.initEgl(holder.getSurface(),null);
                int err2 = GLES20.glGetError();
                DLog.i("EglHelper err2: "+err2);
                mLineGraph = new LineGraph(TestActivity.this);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                DLog.i("EglHelper surfaceChanged");
                mLineGraph.onChange(width,height,0);
                GLES20.glClearColor(1,1,1,1);
                mLineGraph.onDraw(0,0,0,width,height);
                mEglHelper.swapBuffers();
                int err = GLES20.glGetError();
                DLog.i("EglHelper surfaceChanged err: "+err);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DLog.i("EglHelper surfaceDestroyed");
                mLineGraph.onDestroy();
                mEglHelper.destoryEgl();
            }
        });
    }
}
