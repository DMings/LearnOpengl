#version 300 es

precision mediump float;
uniform mat4 vMatrix;
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
out vec2 vTexCoord;

void main() {
     gl_Position  = vPosition;
//     gl_Position  = vMatrix * vPosition;
     vTexCoord = aTextureCoord;
}