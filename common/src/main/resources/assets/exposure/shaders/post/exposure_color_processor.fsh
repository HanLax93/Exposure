#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D InSampler;

layout(location = 0) in vec2 texCoord;

layout(std140) uniform ExposureColorConfig {
    vec4 Mul;
    vec4 Add;
    vec4 Adjustments;
};

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 inTexel = texture(InSampler, texCoord);
    vec3 rgb = inTexel.rgb * Mul.rgb + Add.rgb;

    vec3 gray = vec3(0.3, 0.59, 0.11);
    float luma = dot(rgb, gray);
    vec3 chroma = rgb - luma;
    rgb = chroma * Adjustments.x + luma;
    rgb = (rgb - 0.5) * Adjustments.y + 0.5;

    fragColor = vec4(rgb, 1.0);
}
