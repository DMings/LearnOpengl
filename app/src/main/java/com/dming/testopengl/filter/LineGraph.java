package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.ShaderHelper;

import java.nio.FloatBuffer;

public class LineGraph implements IShader {

    private FloatBuffer mLineFB;
    private int mGraphProgram;

    public LineGraph(Context context) {
        mGraphProgram = ShaderHelper.loadProgram(context, R.raw.line_ver, R.raw.line_frg);
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
    }

    @Override
    public void onChange(int width, int height, int orientation) {

    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mGraphProgram);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, mLineFB);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mLineFB.capacity() / 3);
        GLES20.glDisableVertexAttribArray(0);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mGraphProgram);
    }
}
