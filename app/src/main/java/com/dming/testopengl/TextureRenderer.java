package com.dming.testopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dming.testopengl.utils.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private int mTextureId;
    private int width, height;
    private float mBpRatio;

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


    TextureRenderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        mLineGraph = new LineGraph(mContext);
        mLuminanceFilter = new LuminanceFilter(mContext);
        mNoFilter = new NoFilter(mContext);
        mBlurFilter = new BlurFilter(mContext);
        mSharpenFilter = new SharpenFilter(mContext);
        mBurrFilter = new BurrFilter(mContext);
        mSubduedLightFilter = new SubduedLightFilter(mContext);
        mDarkenLightFilter = new DarkenLightFilter(mContext);
        mSoulFilter = new SoulFilter(mContext);
        mEdgeFilter = new EdgeFilter(mContext);
        //
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
        DLog.i("aspectRatio: " + aspectRatio + " mBpRatio: " + mBpRatio);
        mLineGraph.initShader(width, height, aspectRatio, mBpRatio);
        mLuminanceFilter.initShader(width, height, aspectRatio, mBpRatio);
        mNoFilter.initShader(width, height, aspectRatio, mBpRatio);
        mBlurFilter.initShader(width / 3, height / 3, aspectRatio, mBpRatio);
        mSharpenFilter.initShader(width, height, aspectRatio, mBpRatio);
        mBurrFilter.initShader(width, height, aspectRatio, mBpRatio);
        mSubduedLightFilter.initShader(width, height, aspectRatio, mBpRatio);
        mDarkenLightFilter.initShader(width, height, aspectRatio, mBpRatio);
        mSoulFilter.initShader(width, height, aspectRatio, mBpRatio);
        mEdgeFilter.initShader(width, height, aspectRatio, mBpRatio);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        long time = System.currentTimeMillis();
        int w = width / 3;
        int h = height / 3;
        mLineGraph.onDraw(mTextureId, width, height);
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
            mCurShader.onDraw(mTextureId, 0, 0, width, height);
        }
        DLog.i("time: " + (System.currentTimeMillis() - time));
//        int err = GLES20.glGetError();
//        DLog.i("gl err: " + err);
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
        }else {
            mCurShader = null;
        }
    }

    public void onDestroy() {
        mLineGraph.onDestroy();
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
}
