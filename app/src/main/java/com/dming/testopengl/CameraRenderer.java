package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.dming.testopengl.filter.BlurFilter;
import com.dming.testopengl.filter.BurrFilter;
import com.dming.testopengl.filter.EdgeFilter;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.LuminanceFilter;
import com.dming.testopengl.filter.MultipleFilter;
import com.dming.testopengl.filter.CutApartFilter;
import com.dming.testopengl.filter.ShowGifFilter;
import com.dming.testopengl.filter.ShowMovieFilter;
import com.dming.testopengl.filter.SoulFilter;
import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private int mTextureId;
    private int mWidth, mHeight;

    private LineGraph mLineGraph;
    private CutApartFilter mCutApartFilter;
    private LuminanceFilter mLuminanceFilter;
    private BlurFilter mBlurFilter;
    private ShowGifFilter mShowGifFilter;
    private BurrFilter mBurrFilter;
    private ShowMovieFilter mShowMovieFilter;
    private MultipleFilter mMultipleFilter;
    private SoulFilter mSoulFilter;
    private EdgeFilter mEdgeFilter;
    //
    private IShader mCurShader;
    //
    private GLRunnable mGLRunnable;
    private GLSurfaceView mGLSurfaceView;
    //
    private int mPageIndex = -1;
    private float[] mTexMatrix = new float[16];


    CameraRenderer(GLSurfaceView glSurfaceView, GLRunnable glRunnable) {
        this.mContext = glSurfaceView.getContext();
        this.mGLSurfaceView = glSurfaceView;
        this.mGLRunnable = glRunnable;
        Matrix.setIdentityM(mTexMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        DLog.i("onSurfaceCreated");
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        mTextureId = createOESTexture();
        mGLRunnable.onSurfaceCreated(mTextureId);
        mLineGraph = new LineGraph(mContext);
        mCutApartFilter = new CutApartFilter(mContext);
        mLuminanceFilter = new LuminanceFilter(mContext);
        mBlurFilter = new BlurFilter(mContext);
        mShowGifFilter = new ShowGifFilter(mContext);
        mBurrFilter = new BurrFilter(mContext);
        mShowMovieFilter = new ShowMovieFilter(mGLSurfaceView);
        mMultipleFilter = new MultipleFilter(mContext);
        mSoulFilter = new SoulFilter(mContext);
        mEdgeFilter = new EdgeFilter(mContext);

        chooseOneShaderOfNine(mPageIndex);
    }

    public void onSurfaceCreated(int width, int height) {
        if (this.mWidth != width || this.mHeight != height) {
            this.mWidth = width;
            this.mHeight = height;
            int w = mWidth / 3;
            int h = mHeight / 3;
            mLineGraph.onChange(mWidth, mHeight);
            mCutApartFilter.onChange(w, h);
            mLuminanceFilter.onChange(w, h);
            mBlurFilter.onChange(w, h);
            mShowGifFilter.onChange(mWidth, h);
            mBurrFilter.onChange(w, h);
            mShowMovieFilter.onChange(w, h);
            mMultipleFilter.onChange(w, h);
            mSoulFilter.onChange(w, h);
            mEdgeFilter.onChange(w, h);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        DLog.i("onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        mGLRunnable.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        long time = System.currentTimeMillis();
        int w = mWidth / 3;
        int h = mHeight / 3;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        mLineGraph.onDraw(mTextureId, 0, 0, mWidth, mHeight);
        mCutApartFilter.onDraw(mTextureId, mTexMatrix, 0, h * 2, w, h + 1);
        mLuminanceFilter.onDraw(mTextureId, mTexMatrix, w, 0, w, h);
        mBurrFilter.onDraw(mTextureId, mTexMatrix, 0, h, w, h);
        mShowGifFilter.onDraw(mTextureId, mTexMatrix, w * 2, h * 2, w, h + 1);
        mBlurFilter.onDraw(mTextureId, mTexMatrix, w, h * 2, w, h + 1);
        mShowMovieFilter.onDraw(mTextureId, mTexMatrix, w * 2, h, w + 1, h);
        mMultipleFilter.onDraw(mTextureId, mTexMatrix, w, h, w, h);
        mSoulFilter.onDraw(mTextureId, mTexMatrix, 0, 0, w, h);
        mEdgeFilter.onDraw(mTextureId, mTexMatrix, w * 2, 0, w + 1, h);
//
        if (mCurShader != null) {
            mCurShader.onDraw(mTextureId, mTexMatrix, 0, 0, mWidth, mHeight);
        }
//        DLog.i("time: " + (System.currentTimeMillis() - time));
        int err = GLES20.glGetError();
        if (err != 0) {
            DLog.i("gl err: " + err);
        }
    }

    public void setTexMatrix(float[] mTexMatrix) {
        this.mTexMatrix = mTexMatrix;
    }

    private static int createOESTexture() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

    public void chooseOneShaderOfNine(int index) {
        if (mCurShader == null) {
            mPageIndex = index;
            if (index == 0) {
                mCurShader = mCutApartFilter;
            } else if (index == 1) {
                mCurShader = mBlurFilter;
            } else if (index == 2) {
                mCurShader = mShowGifFilter;
            } else if (index == 3) {
                mCurShader = mBurrFilter;
            } else if (index == 4) {
                mCurShader = mMultipleFilter;
            } else if (index == 5) {
                mCurShader = mShowMovieFilter;
                mShowMovieFilter.playVolume();
            } else if (index == 6) {
                mCurShader = mSoulFilter;
            } else if (index == 7) {
                mCurShader = mLuminanceFilter;
            } else if (index == 8) {
                mCurShader = mEdgeFilter;
            }
        } else {
            if (mPageIndex == 5) {
                mShowMovieFilter.stopVolume();
            }
            mPageIndex = -1;
            mCurShader = null;
        }
    }

    public void onDestroy() {
        this.mWidth = 0;
        this.mHeight = 0;
        this.mCurShader = null;
        if (mLineGraph != null) {
            mLineGraph.onDestroy();
            mLineGraph = null;
        }
        if (mCutApartFilter != null) {
            mCutApartFilter.onDestroy();
            mCutApartFilter = null;
        }
        if (mLuminanceFilter != null) {
            mLuminanceFilter.onDestroy();
            mLuminanceFilter = null;
        }
        if (mBlurFilter != null) {
            mBlurFilter.onDestroy();
            mBlurFilter = null;
        }
        if (mShowGifFilter != null) {
            mShowGifFilter.onDestroy();
            mShowGifFilter = null;
        }
        if (mBurrFilter != null) {
            mBurrFilter.onDestroy();
            mBurrFilter = null;
        }
        if (mShowMovieFilter != null) {
            mShowMovieFilter.onDestroy();
            mShowMovieFilter = null;
        }
        if (mMultipleFilter != null) {
            mMultipleFilter.onDestroy();
            mMultipleFilter = null;
        }
        if (mSoulFilter != null) {
            mSoulFilter.onDestroy();
            mSoulFilter = null;
        }
        if (mEdgeFilter != null) {
            mEdgeFilter.onDestroy();
            mEdgeFilter = null;
        }
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
    }


    public interface GLRunnable {
        void onSurfaceCreated(int textureId);

        void onSurfaceChanged(int width, int height);
    }

}
