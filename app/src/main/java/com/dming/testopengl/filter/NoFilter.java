package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.Matrix;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.GLInterpolator;
import com.dming.testopengl.utils.ShaderHelper;

public class NoFilter extends BaseFilter {

    private GLInterpolator mGLInterpolator;

    public NoFilter(Context context) {
        super(context, R.raw.multiple_frg);
        mGLInterpolator = new GLInterpolator(1000, 1f, 0.0f);
        mTexFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                0, 0.667f,
                0, 0.334f,
                1, 0.334f,
                1, 0.667f,
        });
    }

    @Override
    public void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        int h = height / 3;
        Matrix.setIdentityM(mMvpMatrix, 0);
        super.onDraw(textureId, mMvpMatrix, texMatrix, x, y, width, h);
        Matrix.translateM(mMvpMatrix, 0, mGLInterpolator.getValue() * 2, 0, 0);
        super.onDraw(textureId, mMvpMatrix, texMatrix, x, y + h, width, height - h - h);
        Matrix.setIdentityM(mMvpMatrix, 0);
        super.onDraw(textureId, mMvpMatrix, texMatrix, x, y + height - h, width, h);
    }
}
