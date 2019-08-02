package com.dming.testopengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * @anchor: andy
 * @date: 18-11-10
 */

public class TextureUtils {

    private static final String TAG = "TextureUtils";

    public static int loadTexture(Context context, int resourceId,int t) {
        final int[] textureIds = new int[1];
        //创建一个纹理对象
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //这里需要加载原图未经缩放的数据
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resourceId + " could not be decoded.");
            GLES20.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        Log.e(TAG, "bitmap->" + bitmap.getHeight() + " => " + bitmap.getWidth());
        // 绑定纹理到OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //设置默认的纹理过滤参数
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // 生成MIP贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();
        // 取消绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }

    private void checkGlError(String msg) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new IllegalStateException(
                    msg + ": GLES20 error: 0x" + Integer.toHexString(error));
        }
    }

    public static void loadTexture(Bitmap bitmap, int texture) {
        int target = GLES20.GL_TEXTURE_2D;
        GLES20.glBindTexture(target, texture);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR); //线性插值
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(target, 0);
        bitmap.recycle();
    }

    public static int loadTexture(Context context,int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        return loadTexture(bitmap, GLES20.GL_TEXTURE_2D, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static int loadTexture(Bitmap bitmap, int target, int f_param, int i_param) {
        return loadTexture(bitmap, target, f_param, f_param, i_param, i_param);
    }

    public static int loadTexture(Bitmap bitmap, int target, int min_param, int mag_param, int wrap_s_param, int warp_t_param) {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(target, texture[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, min_param);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, mag_param); //线性插值
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, wrap_s_param);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, warp_t_param);
        GLES20.glBindTexture(target, 0);
        bitmap.recycle();
        return texture[0];
    }

}
