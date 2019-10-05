//#version 100 es

precision mediump float;

attribute vec4 inputPosition;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat4 inputMatrix;
uniform mat4 uTexMatrix;

void main() {
     gl_Position  = inputMatrix * inputPosition;
     textureCoordinate = (uTexMatrix * inputTextureCoordinate).xy;
}