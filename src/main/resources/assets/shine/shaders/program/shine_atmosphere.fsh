#version 150
uniform sampler2D DiffuseSampler; uniform sampler2D MainDepthSampler; uniform float Strength; in vec2 texCoord; out vec4 fragColor;
void main(){ vec4 c=texture(DiffuseSampler,texCoord); float d=texture(MainDepthSampler,texCoord).r; float fog=smoothstep(0.72,1.0,d)*Strength; vec3 haze=vec3(0.72,0.78,0.86); c.rgb=mix(c.rgb,haze,fog); fragColor=c; }
