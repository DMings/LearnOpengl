#version 300 es
uniform mat4 vMatrix;
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
//layout (location = 2) in vec4 aColor;

out vec2 vTexCoord;
//out vec4 ourColor;

void main() {
     gl_Position  = vMatrix * vPosition;
     vTexCoord = aTextureCoord;
//     ourColor=aColor;
}