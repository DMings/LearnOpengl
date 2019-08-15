package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.dming.testopengl.R;

public class SoulFilter extends BaseFilter {

    private int mAlpha;
    private float mScaleRatio = 1.0f;

    public SoulFilter(Context context) {
        super(context, R.raw.soul_frg);
        mAlpha = GLES20.glGetUniformLocation(mProgram, "inputAlpha");
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        Matrix.setIdentityM(mModelMatrix, 0);

        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mImageOESTexture, 0);
        GLES20.glViewport(x, y, width, height);

        Matrix.scaleM(mModelMatrix, 0, 1f, 1.0f, 1f);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glUniform1f(mAlpha, 1.0f);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        Matrix.scaleM(mModelMatrix, 0, mScaleRatio, mScaleRatio, 1f);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glUniform1f(mAlpha, 0.3f);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glDisableVertexAttribArray(mImageOESTexture);
        GLES20.glUseProgram(0);
        GLES20.glDisable(GLES20.GL_BLEND);

        mScaleRatio += 0.030;
        if (mScaleRatio > 1.3f) {
            mScaleRatio = 1.0f;
        }
    }
}
