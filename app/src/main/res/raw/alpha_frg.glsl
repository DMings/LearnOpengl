
precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float inputAlpha;

void main()
{
    gl_FragColor = vec4(texture2D(inputImageTexture, textureCoordinate).rgb, inputAlpha);
}