#version 150
uniform sampler2D DiffuseSampler;
uniform sampler2D MainDepthSampler;
uniform float Strength;
uniform float Thickness;
uniform float DepthThreshold;
uniform float MaxDistance;
in vec2 texCoord; out vec4 fragColor;
float edge(vec2 uv, vec2 d, float center){
  float a=texture(MainDepthSampler,uv+d).r;
  float b=texture(MainDepthSampler,uv-d).r;
  return smoothstep(DepthThreshold*0.15,DepthThreshold,max(abs(a-center),abs(b-center)));
}
void main(){
  vec4 base=texture(DiffuseSampler,texCoord);
  float center=texture(MainDepthSampler,texCoord).r;
  vec2 px=1.0/vec2(textureSize(MainDepthSampler,0));
  float e=0.0;
  e=max(e,edge(texCoord,vec2(px.x*Thickness,0.0),center));
  e=max(e,edge(texCoord,vec2(0.0,px.y*Thickness),center));
  e=max(e,edge(texCoord,vec2(px.x*Thickness,px.y*Thickness),center));
  e=max(e,edge(texCoord,vec2(px.x*Thickness,-px.y*Thickness),center));
  vec3 tint=vec3(1.0,0.86,0.62);
  fragColor=vec4(base.rgb+tint*e*Strength,base.a);
}
