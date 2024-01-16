#type vertex
#version 330 core
layout (location = 0) in vec3 inPos;
layout (location = 1) in vec3 inColor;

out vec3 fColor;

uniform mat4 uProj;
uniform mat4 uView;

void main() {
    fColor = inColor;
    gl_Position = uProj * uView * vec4(inPos, 1.0);
}

#type fragment
#version 330 core
in vec3 fColor;

out vec4 outColor;

void main() {
    outColor = vec4(fColor, 1);
}