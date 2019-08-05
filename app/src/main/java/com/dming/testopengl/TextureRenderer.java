package com.dming.testopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.LuminanceFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer implements GLSurfaceView.Renderer {


    private int mTextureId;
    private float[] mModelMatrix = new float[4 * 4];
    private int width, height;
    private float mBpRatio;

    private LineGraph mLineGraph;
    private LuminanceFilter mLuminanceFilter;
    private Context mContext;

    public TextureRenderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        mLineGraph = new LineGraph(mContext);
        mLuminanceFilter = new LuminanceFilter(mContext);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.t_gl, options);
        mTextureId = TextureUtils.loadTexture(bitmap, GLES20.GL_TEXTURE_2D, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE);
        DLog.i("TextureId: " + mTextureId);
        mBpRatio = 1.0f * bitmap.getWidth() / bitmap.getHeight();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 1f, 1 / aspectRatio, 1f);
        DLog.i("aspectRatio: " + aspectRatio+" mBpRatio: "+mBpRatio);
        mLineGraph.initShader(aspectRatio, mBpRatio);
        mLuminanceFilter.initShader(aspectRatio, mBpRatio);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        int w = width / 3;
        int h = height / 3;
        mLineGraph.onDraw(mTextureId, width, height);
        mLuminanceFilter.onDraw(mTextureId, w, h);
        int err = GLES20.glGetError();
        DLog.i("gl err: " + err);
    }


}
