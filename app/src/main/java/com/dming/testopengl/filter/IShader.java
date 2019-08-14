package com.dming.testopengl.filter;

public interface IShader {

    void setSize(int width, int height);

    void onDraw(int textureId, int x, int y, int width, int height);

    void onDestroy();
}
