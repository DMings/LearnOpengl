package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.ShaderHelper;

public class MultipleFilter extends BaseFilter {

    public MultipleFilter(Context context) {
        super(context, R.raw.multiple_frg);
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
        super.onDraw(textureId, texMatrix, x, y, width, h);
        super.onDraw(textureId, texMatrix, x, y + h, width, height - h - h);
        super.onDraw(textureId, texMatrix, x, y + height - h, width, h);
    }
}
