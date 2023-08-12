#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColour;
layout (location = 2) in vec2 aTextureCoords;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

out vec4 fragmentColour;
out vec2 fragmentTextureCoords;

void main() {
    fragmentColour = aColour;
    fragmentTextureCoords = aTextureCoords;
    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
in vec4 fragmentColour;
in vec2 fragmentTextureCoords;

uniform float uTime;
uniform sampler2D uTexture;

out vec4 colour;

void main() {
    colour = texture(uTexture, fragmentTextureCoords);
}