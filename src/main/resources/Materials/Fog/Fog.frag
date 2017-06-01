#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec4 m_Color;
uniform vec4 m_FogColor;

varying float fogFactor;

void main() {

    gl_FragColor = m_Color;

#ifdef USE_FOG
    gl_FragColor = mix(m_FogColor, gl_FragColor, fogFactor);
#endif

}