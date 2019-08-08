//#version 100 es

precision mediump float;

attribute vec4 inputPosition;
attribute vec2 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat4 inputMatrix;

void main() {
     gl_Position  = inputPosition * inputMatrix;
     textureCoordinate = inputTextureCoordinate;
}