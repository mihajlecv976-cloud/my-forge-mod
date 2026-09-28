package com.bloom.client;

import com.bloom.BloomMod;
import com.bloom.client.config.ShineConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * STATUS: NOT POSSIBLE ON 1.20.1 as a drop-in port.
 *
 * The original Shine 26.2 colored lighting propagates per-color light through
 * the modern light engine and consumes it directly inside the chunk mesh
 * builder / Sodium chunk renderer (see the original mixin group
 * ColoredLightClientLevelMixin / DynamicHeldLightBlockLightEngineMixin /
 * VanillaColoredLightPipelineMixin, listed in MIXINS.md). Vanilla 1.20.1's
 * {@code LightEngine}/{@code LightTexture} only carries a single scalar
 * block-light channel (0-15) baked per-vertex at chunk build time; there is
 * no colored channel to hook, and faking it by tinting the post-process
 * output would not be colored lighting (it would not respect per-block-light
 * source color, would not propagate around corners, and would not affect
 * chunk mesh vertex light).
 *
 * A real port needs one of:
 *  - a full custom light engine (separate RGB light maps + a rewritten
 *    chunk mesh builder that samples them), or
 *  - a dependency on a mod that already exposes colored light data
 *    (e.g. a compatibility layer against a specific colored-lighting mod's
 *    API), which was explicitly out of scope for this backport.
 *
 * This class intentionally does NOT dress up ordinary lighting/bloom as
 * "colored lighting". It only warns once if the feature is enabled in
 * config, so users are not silently misled into thinking it is active.
 */
@Mod.EventBusSubscriber(modid = BloomMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ShineColoredLighting {
    private static boolean warned = false;

    private ShineColoredLighting() {}

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || warned) return;
        if (ShineConfig.coloredLighting) {
            warned = true;
            BloomMod.LOGGER.warn("[Shine] coloredLighting is enabled in config, but real colored light propagation "
                    + "is NOT PORTED on 1.20.1 (requires a custom light engine + chunk mesh rewrite). "
                    + "This toggle currently has no visual effect. See ShineColoredLighting.java / PORT_STATUS.md.");
        }
    }
}
