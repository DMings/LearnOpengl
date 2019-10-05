package com.dming.testopengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.dming.testgif.DLog;
import com.dming.testgif.FGLUtils;
import com.dming.testgif.GifFilter;
import com.dming.testgif.GifPlayer;
import com.dming.testopengl.R;

public class SharpenFilter extends BaseFilter implements IControl, GifPlayer.OnGifListener {

    private GifPlayer mGifPlayer;
    private GifFilter mGifFilter;
    private int mTexture;

    public SharpenFilter(Context context) {
        super(context, R.raw.process_frg);
        mTexture = FGLUtils.createTexture();
        mGifFilter = new GifFilter(context);
        mGifPlayer = new GifPlayer(mTexture);
        mGifPlayer.setOnGifListener(this);
        mGifPlayer.assetPlay(mContext, "mogutou.gif");
    }

    @Override
    public void play() {
        mGifPlayer.resume();
    }

    @Override
    public void pause() {
        mGifPlayer.pause();
    }

    @Override
    public void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        super.onDraw(textureId, texMatrix, x, y, width, height);
        mGifFilter.onDraw(mTexture, x, y, width / 2, height / 2);
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
    public void update() {
//        DLog.i("update>>>");
    }

    @Override
    public void end() {
        DLog.i("end>>>");
    }
}
