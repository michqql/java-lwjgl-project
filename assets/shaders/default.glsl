#type vertex
#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 aColour;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

out vec4 fragmentColour;

void main() {
    fragmentColour = aColour;
    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0, 1.0);
}

#type fragment
#version 330 core
in vec4 fragmentColour;

out vec4 colour;

void main() {
    colour = fragmentColour;
}