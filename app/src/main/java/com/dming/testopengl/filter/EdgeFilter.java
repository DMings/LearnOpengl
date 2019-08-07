package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testopengl.R;

public class EdgeFilter extends BaseFilter{

    public EdgeFilter(Context context) {
        super(context, R.raw.edge_frg);
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        super.onDraw(textureId, x, y, width, height);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
