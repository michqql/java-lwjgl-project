#type vertex
#version 330 core
layout (location = 0) in vec2 vPos;
layout (location = 1) in vec4 vColour;
layout (location = 2) in vec2 vTextureCoords;
layout (location = 3) in float vTextureId;
layout (location = 4) in float vEntityId;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

out vec4 fColour;
out vec2 fTextureCoords;
out float fTextureId;
out float fEntityId;

void main() {
    fColour = vColour;
    fTextureCoords = vTextureCoords;
    fTextureId = vTextureId;
    gl_Position = uProjMatrix * uViewMatrix * vec4(vPos, 1.0, 1.0);
}

#type fragment
#version 330 core
in vec4 fColour;
in vec2 fTextureCoords;
in float fTextureId;
in float fEntityId;

uniform sampler2D uTextures[16];

out vec4 colour;

void main() {
    colour = fColour * texture(uTextures[int(fTextureId)], fTextureCoords);
}