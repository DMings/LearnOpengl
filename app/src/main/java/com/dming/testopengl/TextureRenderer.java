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

    private final FloatBuffer vertexBuffer, mTexVertexBuffer, mColorVertexBuffer;

    private final ShortBuffer mVertexIndexBuffer;

    private int mProgram;

    private int textureId = -1;

    private Context mContext;

    private float[] mModelMatrix = new float[4 * 4];

    private int uMatrixLocation;

    private int mWidth;
    private int mHeight;
    private ByteBuffer mRgbaBuf;
    private Bitmap mBitmap;
    private GLSurfaceView mGLSurfaceView;
    private boolean getImage = false;

    // 镜像180
    /**
     * 顶点坐标
     * (x,y,z)
     */
//    private float[] POSITION_VERTEX = new float[]{
//            -1f, -1f, 0f,    //顶点坐标V0
//            1f, 1f, 0f,      //顶点坐标V1
//            -1f, 1f, 0f,     //顶点坐标V2
//
//            1f, -1f, 0f,     //顶点坐标V3
//            1f, 1f, 0f,      //顶点坐标V4
//            -1f, -1f, 0f     //顶点坐标V5
//    };
    private float[] POSITION_VERTEX = new float[]{
            -0.5f, -0.5f, 0f,    //顶点坐标V0
            0.5f, 0.5f, 0f,      //顶点坐标V1
            -0.5f, 0.5f, 0f,     //顶点坐标V2

            0.5f, -0.5f, 0f,     //顶点坐标V3
            0.5f, 0.5f, 0f,      //顶点坐标V4
            -0.5f, -0.5f, 0f     //顶点坐标V5
    };

    /**
     * 纹理坐标
     * (s,t)
     */
    private static final float[] TEX_VERTEX = {
            0f, 1f,     //纹理坐标V0
            1f, 0f,     //纹理坐标V1
            0f, 0f,     //纹理坐标V2

            1f, 1f,     //纹理坐标V3
            1f, 0f,     //纹理坐标V4
            0f, 1f      //纹理坐标V5
    };

    /**
     * 颜色
     * (R,G,B,A)
     */
    private static final float[] COLOR_VERTEX = {
            1.0f, 0f, 0f, 1f,    //顶点坐标V0
            0f, 1.0f, 0f, 1f,      //顶点坐标V1
            0f, 0f, 1.0f, 1f,     //顶点坐标V2

            1.0f, 1.0f, 0f, 1f,     //顶点坐标V3
            0f, 1.0f, 1.0f, 1f,      //顶点坐标V4
            1.0f, 0f, 1.0f, 1f,     //顶点坐标V5
    };

    /**
     * 索引
     */
    private static final short[] VERTEX_INDEX = {
            0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
            3, 4, 5   //V3,V4,V5 三个顶点组成一个三角形
    };
    private FboUtils fboUtils;

    public TextureRenderer(Context context, GLSurfaceView glSurfaceView) {
        this.mContext = context;
        this.mGLSurfaceView = glSurfaceView;
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

        mColorVertexBuffer = ByteBuffer.allocateDirect(COLOR_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(COLOR_VERTEX);
        mColorVertexBuffer.position(0);

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
        //加载纹理
//        textureId = TextureUtils.loadTexture(this.mContext, R.mipmap.ic_launcher);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        //这里需要加载原图未经缩放的数据
        options.inScaled = false;
//        mBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_launcher, options);
        mBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.test_gl, options);

//        textureId = TextureUtils.loadTexture(mBitmap);
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

        if(fboUtils == null){
            fboUtils = new FboUtils();
            boolean b = fboUtils.createInternal(width,height);
            if(b){
//           int texture = fboUtils.getTextureId();
//           TextureUtils.loadTexture(mBitmap,texture);
                if(textureId == -1){
                    textureId = TextureUtils.loadTexture(mBitmap);
                }
            }else {
                fboUtils = null;
            }
        }
        this.mGLSurfaceView.requestRender();
    }

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

    private boolean hasWrite = false;

    @Override
    public void onDrawFrame(GL10 gl) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        //绑定纹理
        if(fboUtils != null){
            if(!hasWrite){
                hasWrite = true;
                DLog.i("write to FBO--->");
//                int w_fbo = 0;
//                GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, new int[]{w_fbo}, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboUtils.getFrameBufferId());
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                // 设置矩阵
                GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
                //使用程序片段
                GLES20.glUseProgram(mProgram);
                //启用顶点坐标属性
                GLES20.glEnableVertexAttribArray(0);
                GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                //启用纹理坐标属性
                GLES20.glEnableVertexAttribArray(1);
                GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
                //启用颜色属性
                GLES20.glEnableVertexAttribArray(2);
                GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false, 0, mColorVertexBuffer);
                //激活纹理
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_VERTEX.length / 3);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

                GLES20.glDisableVertexAttribArray(0);
                GLES20.glDisableVertexAttribArray(1);
                GLES20.glDisableVertexAttribArray(2);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }else {
                DLog.i("FBO--->");
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                // 设置矩阵
                GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mModelMatrix, 0);
                //使用程序片段
                GLES20.glUseProgram(mProgram);
                //启用顶点坐标属性
                GLES20.glEnableVertexAttribArray(0);
                GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                //启用纹理坐标属性
                GLES20.glEnableVertexAttribArray(1);
                GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexVertexBuffer);
                //启用颜色属性
                GLES20.glEnableVertexAttribArray(2);
                GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false, 0, mColorVertexBuffer);
                //激活纹理
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboUtils.getTextureId());
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_VERTEX.length / 3);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

                GLES20.glDisableVertexAttribArray(0);
                GLES20.glDisableVertexAttribArray(1);
                GLES20.glDisableVertexAttribArray(2);
            }
        }



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

//        int err = GLES20.glGetError();
        getImagePixels();
    }


}
