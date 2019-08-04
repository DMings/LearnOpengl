#version 300 es

uniform mat4 vMatrix;
layout (location = 0) in vec4 vPosition;

out vec4 vPost;

void main() {
    gl_Position  = vMatrix * vPosition;
    vPost = vPosition;
}