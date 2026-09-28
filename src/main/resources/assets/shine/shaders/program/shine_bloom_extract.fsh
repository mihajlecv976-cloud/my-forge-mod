#version 150
/*
 * Soft-knee luminance bloom extract.
 *
 * APPROXIMATION NOTE: the original Shine 26.2 bloom is a per-material
 * "selective source" bloom driven by assets/shine/defaults/shine.json's
 * sourceStrengthOverrides (e.g. sculk=500, lava=75, water=0) rendered into
 * a dedicated bloom-source target during terrain/entity/particle draw
 * (see the retained but unused entity_bloom_source.fsh / terrain_bloom_source.fsh /
 * particle_bloom_source.fsh in shaders/core). That requires hooking chunk
 * section / entity / particle rendering (ChunkSectionsToRenderMixin,
 * RenderTypeMixin, SectionCompilerMixin, SubmitNodeCollectionMixin family)
 * which is NOT implemented here. What IS ported faithfully is the shape of
 * the original tone curve itself: threshold/softKnee/highlightClamp are the
 * same named, same-scale controls as assets/shine/defaults/shine.json
 * (softKnee 0.2, highlightClamp 0.28), applied to plain screen luminance
 * instead of per-material strength.
 */
uniform sampler2D DiffuseSampler;
uniform float Threshold;
uniform float SoftKnee;
uniform float HighlightClamp;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 c = texture(DiffuseSampler, texCoord);
    float l = max(c.r, max(c.g, c.b));

    // Soft-knee curve (Unreal/Unity-style): smooth transition around Threshold
    // instead of a hard cutoff, width controlled by SoftKnee.
    float knee = max(Threshold * SoftKnee, 1e-4);
    float rq = clamp(l - Threshold + knee, 0.0, 2.0 * knee);
    rq = (rq * rq) / (4.0 * knee + 1e-5);
    float contribution = max(rq, l - Threshold);
    float mask = contribution / max(l, 1e-5);

    // HighlightClamp: prevent extremely bright sources (sky/sun) from
    // dominating the bloom buffer relative to the rest of the scene.
    float clampedL = min(l, Threshold + HighlightClamp);
    float finalMask = clamp(mask * (clampedL / max(l, 1e-5)), 0.0, 1.0);

    fragColor = vec4(c.rgb * finalMask, finalMask);
}
