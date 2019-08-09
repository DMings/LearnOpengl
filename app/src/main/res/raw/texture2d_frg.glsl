#extension GL_OES_EGL_image_external : require
precision lowp float;
varying vec2 textureCoordinate;// st xy
uniform sampler2D inputImageTexture;
uniform int isVertical;

void main() {
    vec2 tex_offset =vec2(1.0/300.0, 1.0/300.0);
    vec4 outColor = texture2D(inputImageTexture, textureCoordinate);
    float orAlpha=outColor.a;
    //    float weight[] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
    float weight[5];
    weight[0] = 0.227027;
    weight[1] = 0.1945946;
    weight[2] = 0.1216216;
    weight[3] = 0.054054;
    weight[4] = 0.016216;
    vec3 color=outColor.rgb*weight[0];
    if (isVertical != 0) {
        for (int i=1;i<5;i++) {
            color+=texture2D(inputImageTexture, textureCoordinate+vec2(tex_offset.x * float(i), 0.0)).rgb*weight[i];
            color+=texture2D(inputImageTexture, textureCoordinate-vec2(tex_offset.x * float(i), 0.0)).rgb*weight[i];
        }
    }
    else {
        for (int i=1;i<5;i++) {
            color+=texture2D(inputImageTexture, textureCoordinate+vec2(0.0, tex_offset.y * float(i))).rgb*weight[i];
            color+=texture2D(inputImageTexture, textureCoordinate-vec2(0.0, tex_offset.y * float(i))).rgb*weight[i];
        }
    }
    gl_FragColor=vec4(color, orAlpha);
}