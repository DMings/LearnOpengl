package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testgif.DLog;
import com.dming.testgif.FGLUtils;
import com.dming.testgif.GifFilter;
import com.dming.testgif.GifPlayer;
import com.dming.testopengl.R;

public class ShowGifFilter extends BaseFilter implements GifPlayer.OnGifListener {

    private GifPlayer mGifPlayer;
    private GifFilter mGifFilter;
    private int mTexture;
    private float mGifRatio;

    public ShowGifFilter(Context context) {
        super(context, R.raw.process_frg);
        mTexture = FGLUtils.createTexture();
        mGifFilter = new GifFilter(context);
        mGifPlayer = new GifPlayer(mTexture);
        mGifPlayer.setOnGifListener(this);
        mGifPlayer.assetPlay(mContext, "mogutou.gif");
    }

    public void resume() {
        mGifPlayer.resume();
    }

    public void pause() {
        mGifPlayer.pause();
    }

    @Override
    public void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);// 后面透明
        super.onDraw(textureId, texMatrix, x, y, width, height);
        mGifFilter.onDraw(mTexture, x, y, (int) (mGifRatio * height / 3), height / 3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGifPlayer.destroy();
        mGifFilter.onDestroy();
    }

    @Override
    public void start() {
        DLog.i("start>>>");
    }

    @Override
    public void size(int width, int height) {
        mGifRatio = 1.0f * width / height;
    }

    @Override
    public void update() {
//        DLog.i("update>>>");
    }

    @Override
    public void end() {
        DLog.i("end>>>");
    }
}
