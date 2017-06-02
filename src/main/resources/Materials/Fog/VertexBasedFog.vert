#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"

uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

attribute vec3 inPosition;
attribute vec3 inNormal;

varying vec3 vNormal;
varying vec3 lightVec;
varying vec2 vertexLightValues;
uniform vec4 g_LightDirection;

attribute vec4 inTangent;
varying vec3 vViewDir;
varying vec4 vLightDir;

#ifdef USE_FOG
    uniform vec3 g_CameraPosition;
    
    // 0: linear; 1: exp; 2: exp2;
    // default 0
    uniform int m_FogMode;
    
    // for linear
    uniform vec2 m_FogRange;// vec2(min, max)
    
    // for exp & exp2
    uniform float m_FogDensity;
    const float LOG2 = 1.442695;
#endif

varying float fogFactor;

void main() {
   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;
   
   gl_Position = TransformWorldViewProjection(modelSpacePos);

   vec3 wvPosition = TransformWorldView(modelSpacePos).xyz;
   vec3 wvNormal  = normalize(TransformNormal(modelSpaceNorm));
   vec3 viewDir = normalize(-wvPosition);
   
   vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
   wvLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;
   
   vNormal = wvNormal;
   vViewDir = viewDir;
   lightComputeDir(wvPosition, lightColor.w, wvLightPos, vLightDir, lightVec);
   
   AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
   DiffuseSum  =  m_Diffuse  * vec4(lightColor.rgb, 1.0);
   SpecularSum = (m_Specular * lightColor).rgb;
   
#ifdef USE_FOG
   #ifndef USE_FRAG
   vec4 worldSpacePso = g_WorldMatrix * modelSpacePos;
   float dist = length(worldSpacePso.xyz - g_CameraPosition.xyz);
   
   if (m_FogMode == 0) {
   
       // linear
       fogFactor = (m_FogRange.y - abs(dist)) / (m_FogRange.y - m_FogRange.x);
       
   } else if (m_FogMode == 1) {
   
       // exponential
       fogFactor = exp(-abs(m_FogDensity * dist));
       
   } else {
   
       // exp2 as default
       fogFactor = exp2(-m_FogDensity * m_FogDensity * dist *  dist * LOG2 );
       
   }
   fogFactor = clamp(fogFactor, 0.0, 1.0);
   #endif
#endif
}