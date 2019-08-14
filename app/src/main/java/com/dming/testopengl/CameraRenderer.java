package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.dming.testopengl.filter.AnimationFilter;
import com.dming.testopengl.filter.BlurFilter;
import com.dming.testopengl.filter.BurrFilter;
import com.dming.testopengl.filter.EdgeFilter;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.LuminanceFilter;
import com.dming.testopengl.filter.MultipleFilter;
import com.dming.testopengl.filter.NoFilter;
import com.dming.testopengl.filter.SharpenFilter;
import com.dming.testopengl.filter.SoulFilter;
import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private int mTextureId;
    private int mWidth, mHeight;

    private LineGraph mLineGraph;
    private NoFilter mNoFilter;
    private LuminanceFilter mLuminanceFilter;
    private BlurFilter mBlurFilter;
    private SharpenFilter mSharpenFilter;
    //    private SmoothFilter mSmoothFilter;
    private BurrFilter mBurrFilter;
    //    private SubduedLightFilter mSubduedLightFilter;
    private AnimationFilter mAnimationFilter;
    private MultipleFilter mMultipleFilter;
    //    private DarkenLightFilter mDarkenLightFilter;
    //    private MosaicFilter mMosaicFilter;
    private SoulFilter mSoulFilter;
    private EdgeFilter mEdgeFilter;

    private IShader mCurShader;

    private OnPreviewListener mOnPreviewListener;


    CameraRenderer(Context context, OnPreviewListener onPreviewListener) {
        this.mContext = context;
        this.mOnPreviewListener = onPreviewListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        DLog.i("onSurfaceCreated");
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        this.mWidth = 0;
        this.mHeight = 0;
        mLineGraph = new LineGraph(mContext);
        mNoFilter = new NoFilter(mContext, 90);
        mLuminanceFilter = new LuminanceFilter(mContext, 90);
        mBlurFilter = new BlurFilter(mContext, 90);
        mSharpenFilter = new SharpenFilter(mContext, 90);
        mBurrFilter = new BurrFilter(mContext, 90);
//                mAnimationFilter = new AnimationFilter(mContext, orientation);
//                mAnimationFilter.initPlayer(glSurfaceView);
        mMultipleFilter = new MultipleFilter(mContext, 90);
        mSoulFilter = new SoulFilter(mContext, 90);
        mEdgeFilter = new EdgeFilter(mContext, 90);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        DLog.i("onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        if (this.mWidth != width || this.mHeight != height) {
            this.mWidth = width;
            this.mHeight = height;
            mTextureId = createOESTexture();
            mOnPreviewListener.onSurfaceChanged(mTextureId, new GLRunnable() {
                @Override
                public void run(GLSurfaceView glSurfaceView, int orientation) {
                    int w = mWidth / 3;
                    int h = mHeight / 3;
                    mLineGraph.setSize(mWidth, mHeight);
                    mNoFilter.setSize(w, h);
                    mLuminanceFilter.setSize(w, h);
                    mBlurFilter.setSize(w, h);
                    mSharpenFilter.setSize(mWidth, h);
                    mBurrFilter.setSize(w, h);
//                mAnimationFilter.initShader(w, h);
                    mMultipleFilter.setSize(w, h);
                    mSoulFilter.setSize(w, h);
                    mEdgeFilter.setSize(w, h);
                }
            });
        }
    }

    public void onResume() {
        if (mAnimationFilter != null) {
            mAnimationFilter.play();
        }
    }

    public void onPause() {
        if (mAnimationFilter != null) {
            mAnimationFilter.pause();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        long time = System.currentTimeMillis();
        int w = mWidth / 3;
        int h = mHeight / 3;
        mLineGraph.onDraw(mTextureId, 0, 0, mWidth, mHeight);
        mNoFilter.onDraw(mTextureId, 0, h * 2, w, h);
        mLuminanceFilter.onDraw(mTextureId, w, 0, w, h);
        mBurrFilter.onDraw(mTextureId, 0, h, w, h);
        mSharpenFilter.onDraw(mTextureId, w * 2, h * 2, w, h);
        mBlurFilter.onDraw(mTextureId, w, h * 2, w, h);
//        mAnimationFilter.onDraw(mTextureId, w * 2, h, w, h);
        mMultipleFilter.onDraw(mTextureId, w, h, w, h);
        mSoulFilter.onDraw(mTextureId, 0, 0, w, h);
        mEdgeFilter.onDraw(mTextureId, w * 2, 0, w, h);
//
        if (mCurShader != null) {
            mCurShader.onDraw(mTextureId, 0, 0, mWidth, mHeight);
        }
//        DLog.i("time: " + (System.currentTimeMillis() - time));
        int err = GLES20.glGetError();
        if (err != 0) {
            DLog.i("gl err: " + err);
        }
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
            if (index == 0) {
                mCurShader = mNoFilter;
            } else if (index == 1) {
                mCurShader = mBlurFilter;
            } else if (index == 2) {
                mCurShader = mSharpenFilter;
            } else if (index == 3) {
                mCurShader = mBurrFilter;
            } else if (index == 4) {
                mCurShader = mMultipleFilter;
            } else if (index == 5) {
//                mCurShader = mAnimationFilter;
            } else if (index == 6) {
                mCurShader = mSoulFilter;
            } else if (index == 7) {
                mCurShader = mLuminanceFilter;
            } else if (index == 8) {
                mCurShader = mEdgeFilter;
            }
        } else {
            mCurShader = null;
        }
    }

    public void onDestroy() {
        if (mLineGraph != null) {
            mLineGraph.onDestroy();
            mLineGraph = null;
        }
        if (mNoFilter != null) {
            mNoFilter.onDestroy();
            mNoFilter = null;
        }
        if (mLuminanceFilter != null) {
            mLuminanceFilter.onDestroy();
            mLuminanceFilter = null;
        }
        if (mBlurFilter != null) {
            mBlurFilter.onDestroy();
            mBlurFilter = null;
        }
        if (mSharpenFilter != null) {
            mSharpenFilter.onDestroy();
            mSharpenFilter = null;
        }
        if (mBurrFilter != null) {
            mBurrFilter.onDestroy();
            mBurrFilter = null;
        }
        if (mAnimationFilter != null) {
//        mAnimationFilter.onDestroy();
            mAnimationFilter = null;
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

    public interface OnPreviewListener {
        void onSurfaceChanged(int textureId, GLRunnable runnable);
    }

    public interface GLRunnable {
        void run(GLSurfaceView glSurfaceView, int orientation);
    }

}
