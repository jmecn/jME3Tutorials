#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/BlinnPhongLighting.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"
   
varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

uniform vec4 g_LightDirection;
//varying vec3 vPosition;
varying vec3 vNormal;
varying vec3 vViewDir;
varying vec4 vLightDir;
varying vec3 lightVec;

uniform float m_Shininess;

uniform vec4 m_Color;

#ifdef USE_FOG
    uniform vec4 m_FogColor;
    varying float fogFactor;
    
    #ifdef USE_FRAG
    // 0: linear; 1: exp; 2: exp2;
    // default 0
    uniform int m_FogMode;
    
    // for linear
    uniform vec2 m_FogRange;// vec2(min, max)
    
    // for exp & exp2
    uniform float m_FogDensity;
    const float LOG2 = 1.442695;
    #endif

#endif

void main() {

    vec3 normal = normalize(vNormal);
    vec4 specularColor = vec4(1.0);
    vec4 diffuseColor = vec4(1.0);
    
    vec4 lightDir = vLightDir;
    lightDir.xyz = normalize(lightDir.xyz);
    vec3 viewDir = normalize(vViewDir);
    float spotFallOff = 1.0;
    
    spotFallOff =  computeSpotFalloff(g_LightDirection, lightVec);
    
    vec2 light = computeLighting(normal, viewDir, lightDir.xyz, lightDir.w * spotFallOff, m_Shininess) ;
    
    
    gl_FragColor.rgb =  AmbientSum     * diffuseColor.rgb + 
                        DiffuseSum.rgb * diffuseColor.rgb  * vec3(light.x) +
                        SpecularSum    * specularColor.rgb * vec3(light.y);
                           
#ifdef USE_FOG

    float factor = 1.0;
    
    #ifdef USE_FRAG
        float dist = gl_FragCoord.z / gl_FragCoord.w;
        if (m_FogMode == 0) {
            // linear
            factor = (m_FogRange.y - abs(dist)) / (m_FogRange.y - m_FogRange.x);
        } else if (m_FogMode == 1) {
            // exponential
            factor = exp(-abs(m_FogDensity * dist));
        } else {
            // exp2 as default
            factor = exp2(-m_FogDensity * m_FogDensity * dist *  dist * LOG2 );
        }
        factor = clamp(factor, 0.0, 1.0);
    #else
        factor = fogFactor;
    #endif
    
    gl_FragColor = mix(m_FogColor, gl_FragColor, factor);
#endif

}