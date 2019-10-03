package com.dming.testopengl.filter.test;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testopengl.R;
import com.dming.testopengl.filter.IShader;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;

public class TestLineGraph implements IShader {

    private FloatBuffer mLineFB;
    private int mGraphProgram;
    protected int mPosition;

    public TestLineGraph(Context context) {
        mGraphProgram = ShaderHelper.loadProgram(context, R.raw.test_line_ver, R.raw.test_line_frg);
        mLineFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -1f, 1.0f / 3, 0f,
                1f, 1.0f / 3, 0f,

                -1f, -1.0f / 3, 0f,
                1f, -1.0f / 3, 0f,

                -1.0f / 3, 1, 0f,
                -1.0f / 3, -1, 0f,

                1.0f / 3, 1, 0f,
                1.0f / 3, -1, 0f,
        });
        mPosition = GLES20.glGetAttribLocation(mGraphProgram, "inputPosition");
    }

    @Override
    public void onChange(int width, int height) {

    }

    @Override
    public void onDraw(int textureId, float[] mTexMatrix, int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mGraphProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3, GLES20.GL_FLOAT, false, 0, mLineFB);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mLineFB.capacity() / 3);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mGraphProgram);
    }
}
