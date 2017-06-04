uniform sampler2D m_Texture;
 
varying vec2 texCoord;
 
void main() {
     
    // Convert to grayscale
    vec3 color = texture2D(m_Texture, texCoord).rgb;
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 grayscale = vec3(gray);
     
    gl_FragColor = vec4(grayscale, 1.0);
}