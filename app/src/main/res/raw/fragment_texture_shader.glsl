#version 300 es
precision mediump float;
uniform sampler2D uTextureUnit;
//接收刚才顶点着色器传入的纹理坐标(s,t)
in vec2 vTexCoord;
out vec4 vFragColor;
void main() {
     vFragColor = texture(uTextureUnit,vTexCoord);
}