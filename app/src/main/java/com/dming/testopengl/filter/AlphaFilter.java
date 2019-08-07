package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;

public class AlphaFilter extends BaseFilter {

    private int mAlpha;
    protected FloatBuffer mScalePosFB;

    public AlphaFilter(Context context) {
        super(context, R.raw.alpha_frg);
        mAlpha = GLES20.glGetUniformLocation(mProgram, "inputAlpha");
    }

    @Override
    public void initShader(int width, int height, float viewRatio, float imgRatio) {
        super.initShader(width, height, viewRatio, imgRatio);
        float scale = 1.5f;
        mScalePosFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -viewRatio * imgRatio * scale, 1.0f * scale, 0f,
                -viewRatio * imgRatio * scale, -1.0f * scale, 0f,
                viewRatio * imgRatio * scale, -1.0f * scale, 0f,
                viewRatio * imgRatio * scale, 1.0f * scale, 0f,
        });
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mScalePosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mImageTexture, 0);
        GLES20.glViewport(x, y, width, height);

        GLES20.glUniform1f(mAlpha, 1.0f);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glUniform1f(mAlpha, 0.5f);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mScalePosFB);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glDisableVertexAttribArray(mImageTexture);
        GLES20.glUseProgram(0);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
