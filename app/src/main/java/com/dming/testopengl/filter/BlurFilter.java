package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BlurFilter implements IShader {

    private final ShortBuffer mIndexSB;
    private FloatBuffer mTexFB;
    private final FloatBuffer mFBOTexFB;
    private FloatBuffer mPosFB;
    private FloatBuffer mFBOPosFB;
    private static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };
    private int mOESProgram;
    private int mOESImageTexture;
    private int mOESPosition;
    private int mOESTextureCoordinate;
    private int mOESIsVertical;
    private int mOESMatrix;
    private int uTexOESMatrix;
    // FBO
    private int[] mFrameBuffer = new int[1];
    private int[] mFrameBufferTexture = new int[1];
    private boolean isCreateFBO = false;
    private int mFBOWidth;
    private int mFBOHeight;
    //
    private float[] mIdentityMatrix = new float[4 * 4];
    //
    private int mProgram;
    private int mImageTexture;
    private int mPosition;
    private int mTextureCoordinate;
    private int mIsVertical;
    private int mMatrix;
    private int uTexMatrix;

    public BlurFilter(Context context) {
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mOESProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, R.raw.blur_frg);
        mOESPosition = GLES20.glGetAttribLocation(mOESProgram, "inputPosition");
        mOESTextureCoordinate = GLES20.glGetAttribLocation(mOESProgram, "inputTextureCoordinate");
        mOESImageTexture = GLES20.glGetUniformLocation(mOESProgram, "inputImageOESTexture");
        mOESIsVertical = GLES20.glGetUniformLocation(mOESProgram, "isVertical");
        mOESMatrix = GLES20.glGetUniformLocation(mOESProgram, "inputMatrix");
        uTexOESMatrix = GLES20.glGetUniformLocation(mOESProgram, "uTexMatrix");

        mProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, R.raw.n_blur_frg);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mImageTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        mIsVertical = GLES20.glGetUniformLocation(mProgram, "isVertical");
        mMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
        uTexMatrix = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        mPosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -1, 1.0f, 0f,
                -1, -1.0f, 0f,
                1, -1.0f, 0f,
                1, 1.0f, 0f,
        });
        mFBOPosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -1, 1.0f, 0f,
                -1, -1.0f, 0f,
                1, -1.0f, 0f,
                1, 1.0f, 0f,
        });
        mTexFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                0, 1,
                0, 0,
                1, 0,
                1, 1,
        });
        mFBOTexFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                0, 1,
                0, 0,
                1, 0,
                1, 1,
        });
        Matrix.setIdentityM(mIdentityMatrix, 0);
    }

    @Override
    public void onChange(int width, int height) {
        isCreateFBO = createFBO(width, height);
    }

    @Override
    public void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        onDraw(textureId, mIdentityMatrix, texMatrix, x, y, width, height);
    }

    @Override
    public void onDraw(int textureId, float[] verMatrix, float[] texMatrix, int x, int y, int width, int height) {
        if (isCreateFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
            drawOES(textureId, texMatrix, 0, 0, mFBOWidth, mFBOHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            draw(mFrameBufferTexture[0], verMatrix, x, y, width, height);
        } else {
            drawOES(textureId, texMatrix, x, y, width, height);
        }
    }

    private void draw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mIdentityMatrix, 0);
        GLES20.glUniformMatrix4fv(uTexMatrix, 1, false, texMatrix, 0);
        GLES20.glUniform1i(mIsVertical, 0);
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

    private void drawOES(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        GLES20.glUseProgram(mOESProgram);
        GLES20.glEnableVertexAttribArray(mOESPosition);
        GLES20.glVertexAttribPointer(mOESPosition, 3,
                GLES20.GL_FLOAT, false, 0, mFBOPosFB);
        GLES20.glEnableVertexAttribArray(mOESTextureCoordinate);
        GLES20.glVertexAttribPointer(mOESTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mFBOTexFB);
        GLES20.glUniformMatrix4fv(mOESMatrix, 1, false, mIdentityMatrix, 0);
        GLES20.glUniformMatrix4fv(uTexOESMatrix, 1, false, texMatrix, 0);
        GLES20.glUniform1i(mOESIsVertical, 1);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mOESImageTexture, 0);
        GLES20.glViewport(x, y, width, height);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glDisableVertexAttribArray(mOESPosition);
        GLES20.glDisableVertexAttribArray(mOESTextureCoordinate);
        GLES20.glDisableVertexAttribArray(mOESImageTexture);
        GLES20.glUseProgram(0);
    }

    private boolean createFBO(int width, int height) {
        mFBOWidth = width;
        mFBOHeight = height;
        GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        GLES20.glGenTextures(1, mFrameBufferTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTexture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTexture[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            GLES20.glDeleteTextures(1, mFrameBufferTexture, 0);
            DLog.e("create framebuffer failed");
            return false;
        }
//        DLog.i("Java create framebuffer success: (" +
//                width + ", " + height + "), FB: " + mFrameBuffer[0] + " , Tex: " + mFrameBufferTexture[0]);
        return true;
    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mOESProgram);
        if (isCreateFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0, GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            GLES20.glDeleteTextures(1, mFrameBufferTexture, 0);
        }
    }
}
