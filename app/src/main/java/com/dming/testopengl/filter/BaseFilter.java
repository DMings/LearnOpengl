package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BaseFilter implements IShader {

    protected final ShortBuffer mIndexSB;
    protected final FloatBuffer mTexFB;
    protected FloatBuffer mPosFB;
    protected static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };
    protected static final float[] TEX_VERTEX = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };
    protected int mProgram;

    protected int mPosition;
    protected int mTextureCoordinate;
    protected int mImageTexture;
    protected int mMatrix;
    protected float[] mModelMatrix = new float[4 * 4];

    public BaseFilter(Context context, int resFrgId) {
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX);
        mProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, resFrgId);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mImageTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        mMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
    }

    @Override
    public void initShader(int width, int height,float viewRatio, float imgRatio) {
        mPosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -viewRatio * imgRatio, 1.0f, 0f,
                -viewRatio * imgRatio, -1.0f, 0f,
                viewRatio * imgRatio, -1.0f, 0f,
                viewRatio * imgRatio, 1.0f, 0f,
        });
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    @Override
    public void onDraw(int textureId, int width, int height) {
        onDraw(textureId, 0, 0, width, height);
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mImageTexture, 0);
        GLES20.glViewport(x, y, width, height);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glDisableVertexAttribArray(mImageTexture);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
    }

}
