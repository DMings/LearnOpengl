package com.dming.testgif;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GifFilter {

    protected ShortBuffer mIndexSB;
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
    public static final float[] TEX_VERTEX = {
            0, 0,
            0, 1,
            1, 1,
            1, 0,
    };

    protected int mProgram;
    protected int mPosition;
    protected int mTextureCoordinate;
    protected int mInputImageTexture;
    protected int uMvpMatrix;
    protected int uTexMatrix;
    protected float[] mMvpMatrix = new float[16];
    protected Context mContext;

    public GifFilter(Context context) {
        this.mContext = context;
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mPosFB = ShaderHelper.arrayToFloatBuffer(VERTEX_POS);
        mTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX);
        mProgram = ShaderHelper.loadProgram(context, com.dming.testgif.R.raw.gif_process_ver, com.dming.testgif.R.raw.gif_process_frg);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mInputImageTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        uMvpMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
        uTexMatrix = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        Matrix.setIdentityM(mMvpMatrix, 0);
        FGLUtils.glCheckErr("NoFilter");
    }

    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(uMvpMatrix, 1, false, mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(uTexMatrix, 1, false, mMvpMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mInputImageTexture, 0);
        GLES20.glViewport(x, y, width, height);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glUseProgram(0);
    }

    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
    }

}
