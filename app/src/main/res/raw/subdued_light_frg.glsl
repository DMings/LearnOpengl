precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main()
{
    vec4 baseColor = texture2D(inputImageTexture, textureCoordinate);
    gl_FragColor = 2.0 * baseColor + baseColor * baseColor - 2.0 * baseColor * baseColor;
}
