package com.dming.testopengl.filter;

public interface IShader {

    void onChange(int width, int height, int orientation);

    void onDraw(int textureId, int x, int y, int width, int height);

    void onDestroy();
}
