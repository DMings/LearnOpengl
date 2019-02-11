#version 300 es
uniform mat4 vMatrix;
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec2 aTextureCoord;
//输出纹理坐标(s,t)
out vec2 vTexCoord;
void main() {
     gl_Position  = vMatrix * vPosition;
//     gl_PointSize = 10.0;
     vTexCoord = aTextureCoord;
}