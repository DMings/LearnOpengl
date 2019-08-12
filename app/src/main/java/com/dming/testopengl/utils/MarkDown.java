package com.dming.testopengl.utils;

public class MarkDown {

//    private float[] mViewMatrix = new float[16];
//    private float[] mProjectMatrix = new float[16];
//    private float[] mMVPMatrix = new float[16];
//    public void onSizeChange(int width, int height) {
//        GLES20.glViewport(0, 0, width, height);
//        //设置相机位置
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        //计算变换矩阵
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
//    }

//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
//        GLES20.glClearColor(0, 0, 0, 0);
//        //画图形过程省略，和之前代码一样，可参考完整代码
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
//        GLES20.glDisable(GLES20.GL_BLEND);


//        int id = 0;
//        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, id);
//        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, myVertexBufferSize, null, GLES30.GL_STATIC_DRAW);
//        ByteBuffer mappedBuffer = (ByteBuffer)GLES30.glMapBufferRange(
//                GLES30.GL_PIXEL_PACK_BUFFER,
//                0, this.getWidth() * this.getHeight() * 4,
//                GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT);
//        // [fill buffer...]
//        GLES30.glUnmapBuffer(GLES30.GL_PIXEL_PACK_BUFFER);

//    if (fboUtils == null) {
//        fboUtils = new FboUtils();
//        boolean b = fboUtils.createInternal(width, height);
//        if (b) {
////           int texture = fboUtils.getTextureId();
////           TextureUtils.loadTexture(mBitmap,texture);
//
//        } else {
//            fboUtils = null;
//        }
//    }

//if(fboUtils != null){
//        if(!hasWrite){
//            hasWrite = true;
//            DLog.i("write to FBO--->");
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboUtils.getFrameBufferId());
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//            //使用程序片段
//            GLES20.glUseProgram(mProgram);
//            // 设置矩阵
//            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
//            //启用顶点坐标属性
//            GLES20.glEnableVertexAttribArray(0);
//            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//            //启用纹理坐标属性
//            GLES20.glEnableVertexAttribArray(1);
//            GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
//            //激活纹理
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_VERTEX.length / 3);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            GLES20.glDisableVertexAttribArray(0);
//            GLES20.glDisableVertexAttribArray(1);
//
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//            GLES20.glUseProgram(0);
//        }else {
//            DLog.i("FBO--->"+textureId);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//            //使用程序片段
//            GLES20.glUseProgram(mProgram);
//            // 设置矩阵
//            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
//            //启用顶点坐标属性
//            GLES20.glEnableVertexAttribArray(0);
//            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//            //启用纹理坐标属性
//            GLES20.glEnableVertexAttribArray(1);
//            GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBufferF);
//            //激活纹理
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboUtils.getTextureId());
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_VERTEX.length / 3);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//
//            GLES20.glDisableVertexAttribArray(0);
//            GLES20.glDisableVertexAttribArray(1);
//
//            GLES20.glUseProgram(0);
//        }
//    }

//    public void getImagePixels() {
//        if (getImage) {
//            DLog.i("getImagePixels>>>>>");
//            getImage = false;
//            int width = this.mWidth;
//            int height = this.mHeight;
//            if (mRgbaBuf == null) {
//                mRgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
//            }
//            mRgbaBuf.position(0);
//            long start = System.nanoTime();
//            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 4);
//            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mRgbaBuf);
//            long end = System.nanoTime();
//            float time = 1.0f * (end - start) / 1000000;
//            DLog.d("glReadPixels: " + time + " > " + Thread.currentThread() + " width > " + width + " height > " + height);
//            if (run != null) {
//                run.getData(width, height, mRgbaBuf);
//            }
//        }
//    }
//
//    public void readFBOImagePixels() {
//        if (getImage) {
//            DLog.i("readFBOImagePixels>>>>>");
//            getImage = false;
//            int width = this.mWidth;
//            int height = this.mHeight;
//            if (mRgbaBuf == null) {
//                mRgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
//            }
//            mRgbaBuf.position(0);
//            long start = System.nanoTime();
////            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboUtils.getFrameBufferId());
//
//            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 4);
//            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mRgbaBuf);
//
//            //取消绑定FBO
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
//
//            long end = System.nanoTime();
//            float time = 1.0f * (end - start) / 1000000;
//            DLog.d("glReadPixels: " + time + " > " + Thread.currentThread() + " width > " + width + " height > " + height);
//            if (run != null) {
//                run.getData(width, height, mRgbaBuf);
//            }
//        }
//    }

//    GLES20.glLineWidth(10);
//    gl_PointSize = 10.0;

    //        //生成Render Buffer
//        GLES20.glGenRenderbuffers(1,renderBuffers,0);
//        //绑定Render Buffer
//        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,renderBuffers[0]);
//        //设置为深度的Render Buffer，并传入大小
//        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,GLES20.GL_DEPTH_COMPONENT16,
//                width, height);
//        //为FrameBuffer挂载fRender[0]来存储深度
//        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
//                GLES20.GL_RENDERBUFFER, renderBuffers[0])
//    GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
//    public boolean createInternal(int width, int height) {
//        int[] frameBuffer = new int[1];
//        int[] frameBufferTexture = new int[1];
////        int[] renderBuffers = new int[1];
//
//        // generate frame buffer
//        GLES20.glGenFramebuffers(1, frameBuffer, 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
//
//        // generate texture
//        GLES20.glGenTextures(1, frameBufferTexture, 0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture[0]);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
//                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//        // set texture as colour attachment
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                GLES20.GL_TEXTURE_2D, frameBufferTexture[0], 0);
////
//
//
//        // unbind
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//
//        mFrameBufferId = frameBuffer[0];
//        mFrameBufferTextureId = frameBufferTexture[0];
////        mRenderBufferId = renderBuffers[0];
//
//        mWidth = width;
//        mHeight = height;
//
//        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
//            DLog.e(TAG, "create framebuffer failed");
//            return false;
//        }
//        DLog.e(TAG, "Java create framebuffer success: (" +
//                width + ", " + height + "), FB: " + mFrameBufferId + " , Tex: " + mFrameBufferTextureId+ " , Rend: " + mRenderBufferId);
//        return true;
//    }
}