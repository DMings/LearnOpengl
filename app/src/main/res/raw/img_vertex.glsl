#version 300 es
uniform mat4 vMatrix;
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;

out vec2 vTexCoord;
out vec4 vPost;

void main() {
     gl_Position  = vPosition;
//     gl_Position  = vMatrix * vPosition;
     vTexCoord = aTextureCoord;
     vPost = vPosition;
}