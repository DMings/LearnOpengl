package com.dming.testopengl;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private TextureRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.gl_show);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        // 请求一个OpenGL ES 2.0兼容的上下文
        mGLSurfaceView.setEGLContextClientVersion(2);
        // 设置渲染器(后面会着重讲这个渲染器的类)
        mRenderer = new TextureRenderer(this,mGLSurfaceView);
        mGLSurfaceView.setRenderer(mRenderer);
        // 设置渲染模式为连续模式(会以60fps的速度刷新)
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        initBtnListener();
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

    private void initBtnListener(){
        findViewById(R.id.btn_effect_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRenderer.getHeight();
                mGLSurfaceView.requestRender();
            }
        });
    }

}