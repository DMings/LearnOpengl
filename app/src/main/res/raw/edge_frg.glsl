//#version 120
precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
const float texelOffset = 1.0 / 300.0;
const int coreSize = 3;

void main()
{
    vec4 color = vec4(0.0,0.0,0.0,1.0);
    float kernel[9];
    kernel[6] = 0.0; kernel[7] = -1.0; kernel[8] = 0.0;
    kernel[3] = -1.0; kernel[4] = 4.0; kernel[5] = -1.0;
    kernel[0] = 0.0; kernel[1] = -1.0; kernel[2] = 0.0;

    int index = 0;
    for (int y = 0; y<coreSize; y++) {
        for (int x=0; x<coreSize; x++) {
            vec4 currentColor=texture2D(inputImageTexture, textureCoordinate+vec2(float(-1+x)*texelOffset, float(-1+y)*texelOffset));
            color += currentColor * kernel[index++];
        }
    }
//    if(color.r < 0.1 && color.g < 0.1 && color.b < 0.1){
//        color =  vec4(1.0,1.0,1.0,0.0);
//    }
    gl_FragColor = color;
}
