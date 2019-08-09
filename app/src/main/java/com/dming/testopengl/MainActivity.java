package com.dming.testopengl;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private TextureRenderer mTextureRenderer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.gl_show);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mTextureRenderer = new TextureRenderer(this);
//        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.setRenderer(mTextureRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int w = mGLSurfaceView.getWidth();
                int h = mGLSurfaceView.getHeight();
                int w_3 = w / 3;
                int h_3 = h / 3;
                int x = (int) ((e.getX() - 5) / w_3);
                int y = (int) ((e.getY() - 5) / h_3);
                int index = x + y * 3;
                mTextureRenderer.chooseOneShaderOfNine(index);
                return true;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(this, listener);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

    };

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
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mTextureRenderer.onDestroy();
            }
        });
        super.onDestroy();
    }

}