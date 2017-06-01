#import "Common/ShaderLib/GLSLCompat.glsllib"

//uniform float m_FogDistance;
uniform float m_FogDensity;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;

varying float fogFactor;

const float LOG2 = 1.442695;

void main()  
{
    vec4 modelPosition = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * modelPosition;
    
#ifdef USE_FOG
    vec4 viewSpacePos = g_WorldViewMatrix * modelPosition;
    fogFactor = exp2(-m_FogDensity * m_FogDensity * viewSpacePos.z *  viewSpacePos.z * LOG2 );
    fogFactor = clamp(fogFactor, 0.0, 1.0);
#endif    
    
}