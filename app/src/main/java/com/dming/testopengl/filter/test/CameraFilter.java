package com.dming.testopengl.filter.test;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dming.testopengl.R;
import com.dming.testopengl.camera.CameraTex;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class CameraFilter implements IShader {

    protected final ShortBuffer mIndexSB;
    protected FloatBuffer mTexFB;
    protected FloatBuffer mPosFB;
    protected static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };
    protected static final float[] VERTEX_POS = {
            -1, 1.0f, 0f,
            -1, -1.0f, 0f,
            1, -1.0f, 0f,
            1, 1.0f, 0f,
    };
    protected int mProgram;

    protected int mPosition;
    protected int mTextureCoordinate;
    protected int mImageOESTexture;
    protected int mMatrix;
    protected float[] mModelMatrix = new float[4 * 4];

    public CameraFilter(Context context) {
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, R.raw.camera_frg);
        DLog.i("mProgram: "+mProgram);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mImageOESTexture = GLES20.glGetUniformLocation(mProgram, "inputImageOESTexture");
        mMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
        mPosFB = ShaderHelper.arrayToFloatBuffer(VERTEX_POS);
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    @Override
    public void onChange(int width, int height) {
//        mTexFB = ShaderHelper.arrayToFloatBuffer(CameraTex.getTexVertexByOrientation(orientation));
    }


    @Override
    public void onDraw(int textureId, float[] mTexMatrix, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mImageOESTexture, 0);
        GLES20.glViewport(x, y, width, height);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glDisableVertexAttribArray(mImageOESTexture);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDraw(int textureId, float[] verMatrix, float[] texMatrix, int x, int y, int width, int height) {

    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
    }

}
