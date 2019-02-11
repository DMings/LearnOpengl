package com.dming.testopengl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DMUI";
    private GLSurfaceView mGLSurfaceView;

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
        final TextureRenderer textureRenderer = new TextureRenderer(this);
        mGLSurfaceView.setRenderer(textureRenderer);
        // 设置渲染模式为连续模式(会以60fps的速度刷新)
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        final ImageView mIvShow = findViewById(R.id.iv_show);

        mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLSurfaceView.requestRender();
                ByteBuffer byteBuffer = textureRenderer.sendImage();
                Bitmap bp = Bitmap.createBitmap(textureRenderer.getWidth(), textureRenderer.getHeight(), Bitmap.Config.ARGB_8888);
                bp.copyPixelsFromBuffer(byteBuffer);
                Log.d("TryOpenGL", "Bitmap: " + " width > "+bp.getWidth() + " height > "+bp.getHeight() );
                mIvShow.setImageBitmap(bp);
                mIvShow.invalidate();
            }
        });
        mGLSurfaceView.requestRender();
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

}