package com.dming.testopengl.camera;

public class CameraTex {

    private static final float[] TEX_VERTEX_0 = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,
    };

    private static final float[] TEX_VERTEX_90 = {
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f,
    };

    private static final float[] TEX_VERTEX_180 = {
            1f, 1f,
            1f, 0f,
            0f, 0f,
            0f, 1f,
    };

    private static final float[] TEX_VERTEX_270 = {
            1f, 0f,
            0f, 0f,
            0f, 1f,
            1f, 1f,
    };

    public static float[] getTexVertexByOrientation(int orientation) {
        if (orientation == 90) {
            return TEX_VERTEX_90;
        } else if (orientation == 180) {
            return TEX_VERTEX_180;
        } else if (orientation == 270) {
            return TEX_VERTEX_270;
        } else {
            return TEX_VERTEX_0;
        }
    }

}
