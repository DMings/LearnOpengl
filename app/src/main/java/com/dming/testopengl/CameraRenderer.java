package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.dming.testopengl.filter.BlurFilter;
import com.dming.testopengl.filter.BurrFilter;
import com.dming.testopengl.filter.DarkenLightFilter;
import com.dming.testopengl.filter.EdgeFilter;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.filter.LuminanceFilter;
import com.dming.testopengl.filter.NoFilter;
import com.dming.testopengl.filter.SharpenFilter;
import com.dming.testopengl.filter.SoulFilter;
import com.dming.testopengl.filter.SubduedLightFilter;
import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private int mTextureId;
    private int mWidth, mHeight;
    private float mImgRatio = 0;

    private LineGraph mLineGraph;
    private NoFilter mNoFilter;
    private LuminanceFilter mLuminanceFilter;
    private BlurFilter mBlurFilter;
    private SharpenFilter mSharpenFilter;
    //    private SmoothFilter mSmoothFilter;
    private BurrFilter mBurrFilter;
    private SubduedLightFilter mSubduedLightFilter;
    private DarkenLightFilter mDarkenLightFilter;
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
        GLES20.glClearColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.mWidth = width;
        this.mHeight = height;
        mTextureId = createOESTextureObject();
        mOnPreviewListener.onSurfaceChanged(mTextureId, new GLRunnable() {
            @Override
            public void run(float imgRatio, int orientation) {
                mImgRatio = imgRatio;
                float aspectRatio = mWidth > mHeight ?
                        (float) mWidth / (float) mHeight :
                        (float) mHeight / (float) mWidth;
                DLog.i("aspectRatio: " + aspectRatio + " mImgRatio: " + mImgRatio);
                mLineGraph = new LineGraph(mContext);
                mNoFilter = new NoFilter(mContext, orientation);
                mLuminanceFilter = new LuminanceFilter(mContext, orientation);
                mBlurFilter = new BlurFilter(mContext, orientation);
                mSharpenFilter = new SharpenFilter(mContext, orientation);
                mBurrFilter = new BurrFilter(mContext, orientation);
                mSubduedLightFilter = new SubduedLightFilter(mContext, orientation);
                mDarkenLightFilter = new DarkenLightFilter(mContext, orientation);
                mSoulFilter = new SoulFilter(mContext, orientation);
                mEdgeFilter = new EdgeFilter(mContext, orientation);
                //
                mLineGraph.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mNoFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mLuminanceFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mBlurFilter.initShader(mWidth / 3, mHeight / 3, aspectRatio, mImgRatio);
                mSharpenFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mBurrFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mSubduedLightFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mDarkenLightFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mSoulFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mEdgeFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
            }
        });

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mImgRatio == 0) {
            return;
        }
        long time = System.currentTimeMillis();
        int w = mWidth / 3;
        int h = mHeight / 3;
        mLineGraph.onDraw(mTextureId, mWidth, mHeight);
        mNoFilter.onDraw(mTextureId, 0, h * 2, w, h);
        mLuminanceFilter.onDraw(mTextureId, w, 0, w, h);
        mBurrFilter.onDraw(mTextureId, 0, h, w, h);
        mSharpenFilter.onDraw(mTextureId, w * 2, h * 2, w, h);
        mBlurFilter.onDraw(mTextureId, w, h * 2, w, h);
        mSubduedLightFilter.onDraw(mTextureId, w * 2, h, w, h);
        mDarkenLightFilter.onDraw(mTextureId, w, h, w, h);
        mSoulFilter.onDraw(mTextureId, 0, 0, w, h);
        mEdgeFilter.onDraw(mTextureId, w * 2, 0, w, h);

        if (mCurShader != null) {
            mCurShader.onDraw(mTextureId, 0, 0, mWidth, mHeight);
        }
        DLog.i("time: " + (System.currentTimeMillis() - time));
        int err = GLES20.glGetError();
        if (err != 0) {
            DLog.i("gl err: " + err);
        }
    }

    public static int createOESTextureObject() {
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
                mCurShader = mDarkenLightFilter;
            } else if (index == 5) {
                mCurShader = mSubduedLightFilter;
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
        mLineGraph.onDestroy();
        mNoFilter.onDestroy();
        mLuminanceFilter.onDestroy();
        mBlurFilter.onDestroy();
        mSharpenFilter.onDestroy();
        mBurrFilter.onDestroy();
        mSubduedLightFilter.onDestroy();
        mDarkenLightFilter.onDestroy();
        mSoulFilter.onDestroy();
        mEdgeFilter.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
    }

    public interface OnPreviewListener {
        void onSurfaceChanged(int textureId, GLRunnable runnable);
    }

    public interface GLRunnable {
        void run(float imgRatio, int orientation);
    }

}
