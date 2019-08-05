//#version 110

precision mediump float;

attribute vec4 inputPosition;
attribute vec2 inputTextureCoord;
varying vec2 textureCoordinate;

void main() {
     gl_Position  = inputPosition;
     textureCoordinate = inputTextureCoord;
}