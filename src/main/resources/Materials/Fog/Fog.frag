#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec4 m_Color;
uniform vec4 m_FogColor;
uniform float m_FogDensity;

const float LOG2 = 1.442695;

void main() {

    gl_FragColor = m_Color;

#ifdef USE_FOG
    float dist = gl_FragCoord.z / gl_FragCoord.w;
    
    float fogFactor = exp2(-m_FogDensity * m_FogDensity * dist *  dist * LOG2 );
    gl_FragColor = mix(m_FogColor, gl_FragColor, fogFactor);
#endif

}