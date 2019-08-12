package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.dming.testopengl.filter.LineGraph;
import com.dming.testopengl.utils.DLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestRenderer implements GLSurfaceView.Renderer {

    private Context mContext;
    private int mWidth, mHeight;
    private LineGraph mLineGraph;
    private GLSurfaceView mGLSurfaceView;


    TestRenderer(Context context,GLSurfaceView glSurfaceView) {
        this.mContext = context;
        this.mGLSurfaceView = glSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        DLog.i("onSurfaceChanged:=====> "+Thread.currentThread());
        GLES20.glViewport(0, 0, width, height);
        if (this.mWidth != width || this.mHeight != height) {
            this.mWidth = width;
            this.mHeight = height;
            DLog.i("onSurfaceChanged-: ");
            mLineGraph = new LineGraph(mContext);
            mLineGraph.initShader(mWidth, mHeight, 0, 0);
        }
        mGLSurfaceView.requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mLineGraph == null) {
            return;
        }
        int err1 = GLES20.glGetError();
        if (err1 != 0) {
            DLog.i("gl err1: " + err1);
        }
        DLog.i("onDrawFrame: " + Thread.currentThread());
        mLineGraph.onDraw(1, mWidth, mHeight);
        int err = GLES20.glGetError();
        if (err != 0) {
            DLog.i("gl err: " + err);
        }
    }


    public void onDestroy() {
        mLineGraph.onDestroy();
    }


}
