//#version 110 es

precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main()
{
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    float luminance = dot(textureColor.rgb, W);
//    gl_FragColor = vec4(1.0,0,0,1.0);
    gl_FragColor = vec4(vec3(luminance), textureColor.a);
}
