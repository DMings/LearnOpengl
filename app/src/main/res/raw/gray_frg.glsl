#version 300 es
//#extension GL_OES_EGL_image_external : require
//uniform samplerExternalOES inputImageOESTexture;
precision mediump float;
uniform sampler2D sampler;
in vec2 vTexCoord;// st xy
out vec4 vFragColor;

void main() {
    vec4 fColor = texture(sampler, vTexCoord);
    float fGrayColor = (0.3*fColor.r + 0.59*fColor.g + 0.11*fColor.b);
    vFragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}