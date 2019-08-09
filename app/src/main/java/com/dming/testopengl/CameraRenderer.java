package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.dming.testopengl.filter.CameraFilter;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private int mTextureId;
    private int mWidth, mHeight;
    private float mImgRatio = 0;

    private LineGraph mLineGraph;
//    private NoFilter mNoFilter;
//    private LuminanceFilter mLuminanceFilter;
//    private BlurFilter mBlurFilter;
//    private SharpenFilter mSharpenFilter;
//    //    private SmoothFilter mSmoothFilter;
//    private BurrFilter mBurrFilter;
//    private SubduedLightFilter mSubduedLightFilter;
//    private DarkenLightFilter mDarkenLightFilter;
//    //    private MosaicFilter mMosaicFilter;
//    private SoulFilter mSoulFilter;
//    private EdgeFilter mEdgeFilter;

    private CameraFilter mCameraFilter;

    private IShader mCurShader;

    private OnPreviewListener mOnPreviewListener;


    CameraRenderer(Context context, OnPreviewListener onPreviewListener) {
        this.mContext = context;
        this.mOnPreviewListener = onPreviewListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        mCameraFilter = new CameraFilter(mContext);
        mLineGraph = new LineGraph(mContext);
//        mLuminanceFilter = new LuminanceFilter(mContext);
//        mNoFilter = new NoFilter(mContext);
//        mBlurFilter = new BlurFilter(mContext);
//        mSharpenFilter = new SharpenFilter(mContext);
//        mBurrFilter = new BurrFilter(mContext);
//        mSubduedLightFilter = new SubduedLightFilter(mContext);
//        mDarkenLightFilter = new DarkenLightFilter(mContext);
//        mSoulFilter = new SoulFilter(mContext);
//        mEdgeFilter = new EdgeFilter(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.mWidth = width;
        this.mHeight = height;
        mTextureId = createOESTextureObject();
        DLog.i("OES mTextureId: "+mTextureId);
        mOnPreviewListener.onSurfaceChanged(mTextureId, new GLRunnable() {
            @Override
            public void run(float imgRatio) {
                mImgRatio = imgRatio;
                float aspectRatio = mWidth > mHeight ?
                        (float) mWidth / (float) mHeight :
                        (float) mHeight / (float) mWidth;
                DLog.i("aspectRatio: " + aspectRatio + " mImgRatio: " + mImgRatio);
                mCameraFilter.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
                mLineGraph.initShader(mWidth, mHeight, aspectRatio, mImgRatio);
        //        mLuminanceFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mNoFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mBlurFilter.initShader(width / 3, height / 3, aspectRatio, mImgRatio);
        //        mSharpenFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mBurrFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mSubduedLightFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mDarkenLightFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mSoulFilter.initShader(width, height, aspectRatio, mImgRatio);
        //        mEdgeFilter.initShader(width, height, aspectRatio, mImgRatio);
            }
        });

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(mImgRatio == 0){
            return;
        }
        long time = System.currentTimeMillis();
        int w = mWidth / 3;
        int h = mHeight / 3;
        mCameraFilter.onDraw(mTextureId, mWidth, mHeight);
//        mLineGraph.onDraw(mTextureId, mWidth, mHeight);
//        mNoFilter.onDraw(mTextureId, 0, h * 2, w, h);
//        mLuminanceFilter.onDraw(mTextureId, w, 0, w, h);
//        mBurrFilter.onDraw(mTextureId, 0, h, w, h);
//        mSharpenFilter.onDraw(mTextureId, w * 2, h * 2, w, h);
//        mBlurFilter.onDraw(mTextureId, w, h * 2, w, h);
//        mSubduedLightFilter.onDraw(mTextureId, w * 2, h, w, h);
//        mDarkenLightFilter.onDraw(mTextureId, w, h, w, h);
//        mSoulFilter.onDraw(mTextureId, 0, 0, w, h);
//        mEdgeFilter.onDraw(mTextureId, w * 2, 0, w, h);

        if (mCurShader != null) {
            mCurShader.onDraw(mTextureId, 0, 0, mWidth, mHeight);
        }
//        DLog.i("time: " + (System.currentTimeMillis() - time));
//        int err = GLES20.glGetError();
//        DLog.i("gl err: " + err);
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
//        if (mCurShader == null) {
//            if (index == 0) {
//                mCurShader = mNoFilter;
//            } else if (index == 1) {
//                mCurShader = mBlurFilter;
//            } else if (index == 2) {
//                mCurShader = mSharpenFilter;
//            } else if (index == 3) {
//                mCurShader = mBurrFilter;
//            } else if (index == 4) {
//                mCurShader = mDarkenLightFilter;
//            } else if (index == 5) {
//                mCurShader = mSubduedLightFilter;
//            } else if (index == 6) {
//                mCurShader = mSoulFilter;
//            } else if (index == 7) {
//                mCurShader = mLuminanceFilter;
//            } else if (index == 8) {
//                mCurShader = mEdgeFilter;
//            }
//        }else {
//            mCurShader = null;
//        }
    }

    public void onDestroy() {
        mCameraFilter.onDestroy();
//        mLineGraph.onDestroy();
//        mLuminanceFilter.onDestroy();
//        mBlurFilter.onDestroy();
//        mSharpenFilter.onDestroy();
//        mBurrFilter.onDestroy();
//        mSubduedLightFilter.onDestroy();
//        mDarkenLightFilter.onDestroy();
//        mSoulFilter.onDestroy();
//        mEdgeFilter.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
    }

    public interface OnPreviewListener {
        void onSurfaceChanged(int textureId,GLRunnable runnable);
    }

    public interface GLRunnable{
        void run(float imgRatio);
    }

}
