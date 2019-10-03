package com.dming.testopengl.filter;

public interface IShader {

    void onChange(int width, int height);

    void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height);

    void onDestroy();
}
