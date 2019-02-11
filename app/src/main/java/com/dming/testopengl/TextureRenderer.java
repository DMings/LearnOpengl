package com.dming.testopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

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

    private final FloatBuffer vertexBuffer, mTexVertexBuffer;

    private final ShortBuffer mVertexIndexBuffer;

    private int mProgram;

    private int textureId;

    private Context mContext;

    private float[] mModelMatrix = new float[4 * 4];

    private int uMatrixLocation;

    private int mWidth;
    private int mHeight;
    private ByteBuffer mRgbaBuf;

    // 镜像180
    /**
     * 顶点坐标
     * (x,y,z)
     */
    private float[] POSITION_VERTEX = new float[]{
            -0.5f, -0.5f, 0f,     //顶点坐标V0
            0.5f, 0.5f, 0f,     //顶点坐标V1
            -0.5f, 0.5f, 0f,    //顶点坐标V2

            0.5f, -0.5f, 0f,   //顶点坐标V3
            0.5f, 0.5f, 0f,     //顶点坐标V4
            -0.5f, -0.5f, 0f     //顶点坐标V4
    };

    /**
     * 纹理坐标
     * (s,t)
     */
    private static final float[] TEX_VERTEX = {
            0f, 1f, //纹理坐标V0
            1f, 0f,     //纹理坐标V1
            0f, 0f,     //纹理坐标V2

            1f, 1f,   //纹理坐标V4
            1f, 0f,    //纹理坐标V4
            0f, 1f    //纹理坐标V4
    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
//            0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
//            0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
//            0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
    };

    public TextureRenderer(Context context) {
        this.mContext = context;
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(POSITION_VERTEX);
        vertexBuffer.position(0);

        mTexVertexBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEX_VERTEX);
        mTexVertexBuffer.position(0);

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

    public ByteBuffer sendImage() {
        int width = this.mWidth;
        int height = this.mHeight;
        if(mRgbaBuf == null){
            mRgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
        }
        mRgbaBuf.position(0);
        long start = System.nanoTime();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,mRgbaBuf);
        long end = System.nanoTime();
        Log.d("TryOpenGL", "glReadPixels: " + (end - start) + " > "+Thread.currentThread() + " width > "+width + " height > "+height );
        return mRgbaBuf;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        //编译
        final int vertexShaderId = ShaderHelper.compileVertexShader(ResReadUtils.readResource(this.mContext,R.raw.vertex_texture_shader));
        final int fragmentShaderId = ShaderHelper.compileFragmentShader(ResReadUtils.readResource(this.mContext,R.raw.fragment_texture_shader));
        //链接程序片段
        mProgram = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);

        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //加载纹理
        textureId = TextureUtils.loadTexture(this.mContext, R.mipmap.ic_launcher);
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
        Matrix.scaleM(mModelMatrix,0,1f, 1 / aspectRatio, 1f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
        //使用程序片段
        GLES20.glUseProgram(mProgram);
        //启用顶点坐标属性
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //启用纹理坐标属性
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
        //激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20. GL_ONE_MINUS_SRC_ALPHA);
        // 绘制
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
