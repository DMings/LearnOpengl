package com.dming.testopengl;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;

public class FboUtils {
    private static final String TAG = "FboUtils";
    private int mFrameBufferId = -1;
    private int mFrameBufferTextureId = -1;

    private int mRenderBufferId = -1;

    private int mWidth = 0;
    private int mHeight = 0;

    public boolean createInternal(int width, int height) {
        int[] frameBuffer = new int[1];
        int[] frameBufferTexture = new int[1];
        int[] renderBuffers = new int[1];

        // generate frame buffer
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        // generate texture
        GLES20.glGenTextures(1, frameBufferTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        // set texture as colour attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, frameBufferTexture[0], 0);
//
//        //生成Render Buffer
//        GLES20.glGenRenderbuffers(1,renderBuffers,0);
//        //绑定Render Buffer
//        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,renderBuffers[0]);
//        //设置为深度的Render Buffer，并传入大小
//        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,GLES20.GL_DEPTH_COMPONENT16,
//                width, height);
//        //为FrameBuffer挂载fRender[0]来存储深度
//        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
//                GLES20.GL_RENDERBUFFER, renderBuffers[0]);

        // unbind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        mFrameBufferId = frameBuffer[0];
        mFrameBufferTextureId = frameBufferTexture[0];
        mRenderBufferId = renderBuffers[0];

        mWidth = width;
        mHeight = height;

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            DLog.e(TAG, "create framebuffer failed");
            return false;
        }
        DLog.e(TAG, "Java create framebuffer success: (" +
                width + ", " + height + "), FB: " + mFrameBufferId + " , Tex: " + mFrameBufferTextureId+ " , Rend: " + mRenderBufferId);
        return true;
    }

    public void beginDrawToFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
//        GLES20.glClearColor(0, 0, 0, 1);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public void endDrawToFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawToFrameBuffer(Runnable runnable) {
        beginDrawToFrameBuffer();
        runnable.run();
        endDrawToFrameBuffer();
    }

    public int getTextureId() {
        return mFrameBufferTextureId;
    }

    public int getFrameBufferId() {
        return mFrameBufferId;
    }

    public int getRenderBufferId() {
        return mRenderBufferId;
    }

    public void release(boolean deleteBuffer) {
        if (deleteBuffer && mFrameBufferId != -1) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferId);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, 0, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            GLES20.glDeleteFramebuffers(1, new int[] {mFrameBufferId}, 0);
        }

        if (mFrameBufferTextureId != -1) {
            GLES20.glDeleteTextures(1, new int[] {mFrameBufferTextureId}, 0);
        }

        mWidth = 0;
        mHeight = 0;
        mFrameBufferId = -1;
        mFrameBufferTextureId = -1;
    }

}

