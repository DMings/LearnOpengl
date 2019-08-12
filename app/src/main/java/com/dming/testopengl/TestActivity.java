package com.dming.testopengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dming.testopengl.utils.DLog;

public class TestActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private TestRenderer mTestRenderer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.gl_show);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mTestRenderer = new TestRenderer(this,mGLSurfaceView);
        mGLSurfaceView.setRenderer(mTestRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        DLog.e("onDestroy========================================>>>");
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mTestRenderer.onDestroy();
            }
        });
        super.onDestroy();
    }

}
