package com.dming.testopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer implements GLSurfaceView.Renderer {

    private final FloatBuffer mTexFB;
    private FloatBuffer mLineFB;
    private FloatBuffer mImgFB;
    private final ShortBuffer mIndexSB;
    private int mImgProgram;
    private int mGraphProgram;
    private int mProLuminance;
    private int textureId = -1;
    private float[] mModelMatrix = new float[4 * 4];
    private Context mContext;
    private int width, height;
    private float bpRatio = 1.0f;

    private static final float[] TEX_VERTEX = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };

    private static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };

    public TextureRenderer(Context context) {
        this.mContext = context;
        mTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX);
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
    }

    private void initByteBuffer(int width, int height) {
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        DLog.i("aspectRatio: " + aspectRatio);
        mLineFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -1f, aspectRatio / 3, 0f,
                1f, aspectRatio / 3, 0f,

                -1f, -aspectRatio / 3, 0f,
                1f, -aspectRatio / 3, 0f,

                -1.0f / 3, aspectRatio, 0f,
                -1.0f / 3, -aspectRatio, 0f,

                1.0f / 3, aspectRatio, 0f,
                1.0f / 3, -aspectRatio, 0f,
        });
        mImgFB = ShaderHelper.arrayToFloatBuffer(new float[]{
                -aspectRatio * bpRatio, 1.0f, 0f,
                -aspectRatio * bpRatio, -1.0f, 0f,

                aspectRatio * bpRatio, -1.0f, 0f,
                aspectRatio * bpRatio, 1.0f, 0f,
//                -1f, 1.0f / 3 + 2.0f / 3 / aspectRatio, 0f,
//                -1f, 1.0f / 3 , 0f,
//
//                -1.0f / 3, 1.0f / 3, 0f,
//                -1.0f / 3, 1.0f / 3 + 2.0f / 3 / aspectRatio, 0f,
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);

        mImgProgram = ShaderHelper.loadProgram(this.mContext, R.raw.img_vertex, R.raw.img_fragment);
        mGraphProgram = ShaderHelper.loadProgram(this.mContext, R.raw.graph_vertex, R.raw.graph_fragment);

        mProLuminance = ShaderHelper.loadProgram(this.mContext, R.raw.process_ver, R.raw.luminance_frg);
        DLog.i("mProLuminance: "+mProLuminance);
        int err = GLES20.glGetError();
        DLog.i("gl err: " + err);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.t_gl, options);
        textureId = TextureUtils.loadTexture(bitmap, GLES20.GL_TEXTURE_2D, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE);

        bpRatio = 1.0f * bitmap.getWidth() / bitmap.getHeight();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 1f, 1 / aspectRatio, 1f);
        initByteBuffer(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mGraphProgram);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mGraphProgram, "vMatrix"), 1, false, mModelMatrix, 0);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, mLineFB);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mLineFB.capacity() / 3);
        GLES20.glDisableVertexAttribArray(0);

        GLES20.glUseProgram(mImgProgram);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mImgProgram, "vMatrix"), 1, false, mModelMatrix, 0);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, mImgFB);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mImgProgram, "sampler"), 0);
        for (int i = 1;i < 3;i++){
            for (int j = 0;j < 3;j++){
                GLES20.glViewport(width / 3 * i, height / 3 * j, width / 3, height / 3);
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                        GLES20.GL_UNSIGNED_SHORT, mIndexSB);
            }
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(0);
        GLES20.glDisableVertexAttribArray(1);
        GLES20.glUseProgram(0);

        int err0 = GLES20.glGetError();
        DLog.i("gl err0: " + err0);

        GLES20.glUseProgram(mProLuminance);
        int inputPosition = GLES20.glGetAttribLocation(mProLuminance,"inputPosition");
        GLES20.glEnableVertexAttribArray(inputPosition);
        GLES20.glVertexAttribPointer(inputPosition, 3,
                GLES20.GL_FLOAT, false, 0, mImgFB);
        int inputTextureCoord = GLES20.glGetAttribLocation(mProLuminance,"inputTextureCoord");
        GLES20.glEnableVertexAttribArray(inputTextureCoord);
        GLES20.glVertexAttribPointer(inputTextureCoord, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProLuminance, "inputImageTexture"), 0);

        GLES20.glViewport(0, 0, width / 3, height / 3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDisableVertexAttribArray(inputPosition);
        GLES20.glDisableVertexAttribArray(inputTextureCoord);
        GLES20.glUseProgram(0);

        int err = GLES20.glGetError();
        DLog.i("gl err: " + err);

    }


}
