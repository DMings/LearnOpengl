#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES inputImageOESTexture;
uniform int isVideo;

void main() {
    vec4 color = texture2D(inputImageOESTexture, textureCoordinate);
    if (isVideo != 0){
        if (color.r < 0.1 && color.g < 0.1 && color.b < 0.1){
            color = vec4(0, 0, 0, 0);
        }
    }
    gl_FragColor = color;
}