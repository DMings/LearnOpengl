package com.dming.testopengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class StretchUtils {

    private static int uMatrixLocation;
    /**
     * 矩阵数组
     */
    private static final float[] mProjectionMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    public static void onSurfaceCreated(int program) {
        uMatrixLocation = GLES20.glGetUniformLocation(program,"u_Matrix");
    }

    public static void onSurfaceChanged(int width, int height) {
        // 边长比(>=1)，非宽高比
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        // 1. 矩阵数组
        // 2. 结果矩阵起始的偏移量
        // 3. left：x的最小值
        // 4. right：x的最大值
        // 5. bottom：y的最小值
        // 6. top：y的最大值
        // 7. near：z的最小值
        // 8. far：z的最大值
        if (width > height) {
            // 横屏
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // 竖屏or正方形
            Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        // 更新u_Matrix的值，即更新矩阵数组
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0);
    }

//    + "uniform mat4 u_Matrix;\n"
}
