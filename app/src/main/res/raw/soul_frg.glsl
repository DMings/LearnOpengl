#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES inputImageOESTexture;
uniform float inputAlpha;

void main()
{
    gl_FragColor = vec4(texture2D(inputImageOESTexture, textureCoordinate).rgb, inputAlpha);
}