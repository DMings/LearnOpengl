package com.dming.testopengl.filter;

public interface IShader {

    void initShader(int width, int height,float viewRatio, float imgRatio);

    void onDraw(int textureId, int width, int height);

    void onDraw(int textureId, int x, int y, int width, int height);

    void onDestroy();
}
