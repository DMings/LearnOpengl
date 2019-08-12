#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES inputImageOESTexture;

void main()
{
    vec4 baseColor = texture2D(inputImageOESTexture, textureCoordinate);
    gl_FragColor = 0.7 *  baseColor;
}