//#version 110

precision mediump float;

attribute vec4 inputPosition;
attribute vec2 inputTextureCoordinate;
varying vec2 textureCoordinate;

void main() {
     gl_Position  = inputPosition;
     textureCoordinate = inputTextureCoordinate;
}