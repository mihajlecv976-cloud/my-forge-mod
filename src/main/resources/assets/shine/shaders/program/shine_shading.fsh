#version 150
uniform sampler2D DiffuseSampler; uniform float Strength; in vec2 texCoord; out vec4 fragColor;
void main(){ vec4 c=texture(DiffuseSampler,texCoord); float l=dot(c.rgb,vec3(0.299,0.587,0.114)); float shadow=1.0-(1.0-l)*Strength; c.rgb*=shadow; c.rgb+=c.rgb*(1.0-c.rgb)*Strength*0.08; fragColor=c; }
