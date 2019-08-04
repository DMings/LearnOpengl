package com.dming.testopengl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private TextureRenderer mRenderer;
    private ImageView mIvShowTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = findViewById(R.id.gl_show);
        mIvShowTx = findViewById(R.id.iv_show_tx);
//        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        // 请求一个OpenGL ES 2.0兼容的上下文
        mGLSurfaceView.setEGLContextClientVersion(2);
        mRenderer = new TextureRenderer(this);
        mGLSurfaceView.setRenderer(mRenderer);
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

    private void initBtnListener() {
        findViewById(R.id.btn_effect_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLSurfaceView.requestRender();
            }
        });
    }

}