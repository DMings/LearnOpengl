package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BlurFilter implements IShader {

    private final ShortBuffer mIndexSB;
    private final FloatBuffer mTexFB;
    private final FloatBuffer mFBOTexFB;
    private FloatBuffer mPosFB;
    private FloatBuffer mFBOPosFB;
    private static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };
    private static final float[] TEX_VERTEX = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };
    private static final float[] TEX_VERTEX_FBO = {
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f,
    };
    private int mProgram;

    private int mPosition;
    private int mTextureCoordinate;
    private int mImageTexture;
    private int mIsVertical;
    // FBO
    private int[] mFrameBuffer = new int[1];
    private int[] mFrameBufferTexture = new int[1];
    private boolean isCreateFBO = false;
    private int mFBOWidth;
    private int mFBOHeight;
    //
    protected int mMatrix;
    protected float[] mModelMatrix = new float[4 * 4];

    public BlurFilter(Context context) {
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX);
        mFBOTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX_FBO);
        mProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, R.raw.blur_frg);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mImageTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        mIsVertical = GLES20.glGetUniformLocation(mProgram, "isVertical");
        mMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
    }

    @Override
    public void initShader(int width, int height, float viewRatio, float imgRatio) {
        mPosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -viewRatio * imgRatio, 1.0f, 0f,
                -viewRatio * imgRatio, -1.0f, 0f,
                viewRatio * imgRatio, -1.0f, 0f,
                viewRatio * imgRatio, 1.0f, 0f,
        });
        mFBOPosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -1, 1.0f, 0f,
                -1, -1.0f, 0f,
                1, -1.0f, 0f,
                1, 1.0f, 0f,
        });
        isCreateFBO = createFBO((int) (width * viewRatio * imgRatio), height);
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    @Override
    public void onDraw(int textureId, int width, int height) {
        onDraw(textureId, 0, 0, width, height);
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        if (isCreateFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
            drawFBO(textureId, 0, 0, mFBOWidth, mFBOHeight);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            draw(mFrameBufferTexture[0], x, y, width, height);
        } else {
            draw(textureId, x, y, width, height);
        }
    }

    private void draw(int textureId, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
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

    private void drawFBO(int textureId, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mFBOPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mFBOTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glUniform1i(mIsVertical, 1);
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
        GLES20.glDeleteProgram(mProgram);
        if (isCreateFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
            GLES20.glDeleteTextures(1, mFrameBufferTexture, 0);
        }
    }
}
