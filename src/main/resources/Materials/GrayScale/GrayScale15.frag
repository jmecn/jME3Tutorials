#import "Common/ShaderLib/MultiSample.glsllib"
 
uniform COLORTEXTURE m_Texture;
  
in vec2 texCoord;
out vec4 fragColor;
  
void main() {
      
    // Convert to grayscale
    vec3 color = getColor(m_Texture, texCoord).rgb;
    float gray = (color.r + color.g + color.b) / 3.0;
    vec3 grayscale = vec3(gray);
      
    fragColor = vec4(grayscale, 1.0);
}