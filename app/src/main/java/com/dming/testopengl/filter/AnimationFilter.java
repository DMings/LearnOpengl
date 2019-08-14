package com.dming.testopengl.filter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.dming.testopengl.R;
import com.dming.testopengl.utils.DLog;
import com.dming.testopengl.utils.ShaderHelper;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.dming.testopengl.camera.CameraTex.TEX_VERTEX_90;

public class AnimationFilter extends BaseFilter {

    private int mIsVideo;
    private FloatBuffer mVideoTexFB;
    // 
    private MediaPlayer mPlayer;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private int mOESVideoTexture = -1;

    public AnimationFilter(Context context, int orientation) {
        super(context, R.raw.animation_frg, orientation);
        mIsVideo = GLES20.glGetUniformLocation(mProgram, "isVideo");
        mVideoTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX_90);
    }

    @Override
    public void onDraw(int textureId, int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mModelMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(mImageOESTexture, 0);

        GLES20.glUniform1i(mIsVideo, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glViewport(x, y, width, height);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mVideoTexFB);
        GLES20.glUniform1i(mIsVideo, 1);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESVideoTexture);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDestroy() {
        release();
        GLES20.glDeleteBuffers(1, new int[]{mOESVideoTexture}, 0);
        super.onDestroy();
    }

    public void play() {
        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        }
    }

    public void pause() {
        if (null != mPlayer) {
            mPlayer.pause();
        }
    }


    public void initPlayer(final GLSurfaceView glSurfaceView) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (mPlayer != null) {
                        if (!mPlayer.isPlaying()) {
                            mPlayer.start();
                            mPlayer.setLooping(true);
                        }
                    }
                }
            });
            try {
                mPlayer.reset();
                AssetFileDescriptor afd = this.mContext.getAssets().openFd("animation.mp4");
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mPlayer.prepareAsync(); // 异步的准备方法
            } catch (IOException e) {
                DLog.i("Play Error!!!");
            }
            mOESVideoTexture = createOESTextureObject();
            mSurfaceTexture = new SurfaceTexture(mOESVideoTexture);
            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
                    if (mSurfaceTexture != null) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mSurfaceTexture != null) {
                                    mSurfaceTexture.updateTexImage();
                                }
                            }
                        });
                    }
                }
            });
            mSurface = new Surface(mSurfaceTexture);
            mPlayer.setSurface(mSurface);
            DLog.i("Video oesTexture: " + mOESVideoTexture);
        }
    }


    public void release() {
        mPlayer.stop();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(null);
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mPlayer.release();
        mSurface.release();
        mPlayer = null;
    }


    public int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }
}
