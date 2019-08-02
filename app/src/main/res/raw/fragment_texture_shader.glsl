#version 300 es
precision mediump float;
uniform sampler2D sampler;
in vec2 vTexCoord;
//in vec4 ourColor;
out vec4 vFragColor;

void main() {
    vec2 tex_offset =vec2(1.0/300.0, 1.0/300.0);
    vec4 fColor = texture(sampler, vTexCoord);
//    float fGrayColor = (0.3*fColor.r + 0.59*fColor.g + 0.11*fColor.b);
//    vec4 outColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
    //     vFragColor = texture(uTextureUnit,vTexCoord)* ourColor;
    vFragColor = fColor;
//    vec4 outColor = fColor;
//
//    const bool isVertical = false;
//    float orAlpha=outColor.a;
//    float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
//    vec3 color=outColor.rgb*weight[0];
//    if (!isVertical)
//    {
//        for (int i=1;i<5;i++)
//        {
//            color+=texture(sampler, vTexCoord+vec2(tex_offset.x * float(i), 0.0)).rgb*weight[i];
//            color+=texture(sampler, vTexCoord-vec2(tex_offset.x * float(i), 0.0)).rgb*weight[i];
//
//        }
//    }
//    else
//    {
//        for (int i=1;i<5;i++)
//        {
//            color+=texture(sampler, vTexCoord+vec2(0.0, tex_offset.y * float(i))).rgb*weight[i];
//            color+=texture(sampler, vTexCoord-vec2(0.0, tex_offset.y * float(i))).rgb*weight[i];
//        }
//    }
//    vFragColor=vec4(color, orAlpha);
}