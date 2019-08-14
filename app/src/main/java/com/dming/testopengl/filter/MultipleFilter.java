package com.dming.testopengl.filter;

import android.content.Context;

import com.dming.testopengl.R;
import com.dming.testopengl.camera.CameraTex;
import com.dming.testopengl.utils.ShaderHelper;

public class MultipleFilter extends BaseFilter {

    public MultipleFilter(Context context, int orientation) {
        super(context, R.raw.multiple_frg, orientation);
        mTexFB = ShaderHelper.arrayToFloatBuffer(CameraTex.getHTexVertexByOrientation(orientation));
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        int h = height / 3;
        super.onDraw(textureId, x, y, width, h);
        super.onDraw(textureId, x, y + h, width, height - h - h);
        super.onDraw(textureId, x, y + height - h, width, h);
    }
}
