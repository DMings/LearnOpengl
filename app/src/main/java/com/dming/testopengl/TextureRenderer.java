package com.dming.testopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @anchor: andy
 * @date: 2018-11-09
 * @description: 基于纹理贴图显示bitmap
 */
public class TextureRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "TextureRenderer";

    private final FloatBuffer vertexBuffer,vertexBuffer2, mTexVertexBuffer, mTexVertexBufferF;

    private final ShortBuffer mVertexIndexBuffer;

    private int mProgram;

    private int textureId = -1;
    private int texture2Id = -1;

    private Context mContext;

    private float[] mModelMatrix = new float[4 * 4];

    private int uMatrixLocation;

    private int mWidth;
    private int mHeight;
    private ByteBuffer mRgbaBuf;
    private GLSurfaceView mGLSurfaceView;
    private boolean getImage = false;

    private float[] POSITION_VERTEX = new float[]{
            -0.9f, 0.9f, 0f,
            -0.9f, 0.1f, 0f,
            -0.1f, 0.1f, 0f,
            -0.1f, 0.9f, 0f,
    };

    private float[] POSITION_VERTEX2 = new float[]{
            0.1f, 0.9f, 0f,
            0.1f, 0.1f, 0f,
            0.9f, 0.1f, 0f,
            0.9f, 0.9f, 0f,
    };

    private static final float[] TEX_VERTEX = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };

    private static final float[] TEX_VERTEX_F = {

            0f, 0f,     //纹理坐标V2
            1f, 1f,     //纹理坐标V3
            0f, 1f,     //纹理坐标V0

            1f, 0f,     //纹理坐标V3
            1f, 1f,     //纹理坐标V4
            0f, 0f      //纹理坐标V5

    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };

    public TextureRenderer(Context context, GLSurfaceView glSurfaceView) {
        this.mContext = context;
        this.mGLSurfaceView = glSurfaceView;

        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(POSITION_VERTEX);
        vertexBuffer.position(0);

        vertexBuffer2 = ByteBuffer.allocateDirect(POSITION_VERTEX2.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(POSITION_VERTEX2);
        vertexBuffer2.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);

        mTexVertexBufferF = ByteBuffer.allocateDirect(TEX_VERTEX_F.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX_F);
        mTexVertexBufferF.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


    private Run run;

    public abstract static class Run {
        public abstract void getData(int w, int h, ByteBuffer byteBuffer);
    }

    public void setRun(Run run) {
        this.run = run;
    }

    public void setGetImage() {
        getImage = true;
    }

    public void getImagePixels() {
        if (getImage) {
            DLog.i("getImagePixels>>>>>");
            getImage = false;
            int width = this.mWidth;
            int height = this.mHeight;
            if (mRgbaBuf == null) {
                mRgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
            }
            mRgbaBuf.position(0);
            long start = System.nanoTime();
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mRgbaBuf);
            long end = System.nanoTime();
            float time = 1.0f * (end - start) / 1000000;
            DLog.d("glReadPixels: " + time + " > " + Thread.currentThread() + " width > " + width + " height > " + height);
            if (run != null) {
                run.getData(width, height, mRgbaBuf);
            }
        }
    }

    public void readFBOImagePixels() {
        if (getImage) {
            DLog.i("readFBOImagePixels>>>>>");
            getImage = false;
            int width = this.mWidth;
            int height = this.mHeight;
            if (mRgbaBuf == null) {
                mRgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
            }
            mRgbaBuf.position(0);
            long start = System.nanoTime();
//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboUtils.getFrameBufferId());

            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 4);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mRgbaBuf);

            //取消绑定FBO
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);

            long end = System.nanoTime();
            float time = 1.0f * (end - start) / 1000000;
            DLog.d("glReadPixels: " + time + " > " + Thread.currentThread() + " width > " + width + " height > " + height);
            if (run != null) {
                run.getData(width, height, mRgbaBuf);
            }
        }
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        //编译
        final int vertexShaderId = ShaderHelper.compileVertexShader(ResReadUtils.readResource(this.mContext, R.raw.vertex_texture_shader));
        final int fragmentShaderId = ShaderHelper.compileFragmentShader(ResReadUtils.readResource(this.mContext, R.raw.fragment_texture_shader));
        //链接程序片段
        mProgram = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "vMatrix");


        textureId = TextureUtils.loadTexture(this.mContext,R.drawable.test_gl);
        texture2Id = TextureUtils.loadTexture(this.mContext,R.mipmap.ic_launcher);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.mWidth = width;
        this.mHeight = height;
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 1f, 1 / aspectRatio, 1f);
//        this.mGLSurfaceView.requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
        //激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2Id);

        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer2);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(0);
        GLES20.glDisableVertexAttribArray(1);

        GLES20.glUseProgram(0);

        int err = GLES20.glGetError();
        DLog.i("gl err: " + err);

//        readFBOImagePixels();
    }


}
