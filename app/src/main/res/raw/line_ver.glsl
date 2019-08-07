#version 300 es

layout (location = 0) in vec4 inputPosition;

void main() {
    gl_Position  = inputPosition;
}