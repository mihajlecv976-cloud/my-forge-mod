#version 150
uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
in vec2 texCoord; out vec4 fragColor;
void main(){ vec2 a=vec2(1.384615/OutSize.x,0.0); vec2 b=vec2(3.230769/OutSize.x,0.0); vec3 c=texture(DiffuseSampler,texCoord).rgb*0.227027; c+=texture(DiffuseSampler,texCoord+a).rgb*0.316216; c+=texture(DiffuseSampler,texCoord-a).rgb*0.316216; c+=texture(DiffuseSampler,texCoord+b).rgb*0.070270; c+=texture(DiffuseSampler,texCoord-b).rgb*0.070270; fragColor=vec4(c,1.0); }
