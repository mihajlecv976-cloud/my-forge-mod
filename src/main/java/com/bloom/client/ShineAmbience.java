package com.bloom.client;

import com.bloom.BloomMod;
import com.bloom.client.config.ShineConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Lightweight 1.20.1 replacement for Shine's client-side ambient particle spawners. */
@Mod.EventBusSubscriber(modid = BloomMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ShineAmbience {
    private ShineAmbience() {}

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ShineConfig.ambientParticles) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        if (mc.level.random.nextFloat() > 0.18f) return;

        BlockPos pos = mc.player.blockPosition();
        boolean forest = mc.level.getBiome(pos).is(BiomeTags.IS_FOREST) || mc.level.getBiome(pos).is(BiomeTags.IS_JUNGLE);
        if (forest && mc.level.random.nextFloat() < 0.70f) {
            spawn(mc, "world_ambience_falling_leaf", 5.0, 2.0, 5.0, 0.0, -0.01, 0.0);
        }

        if (mc.level.isRainingAt(pos) && mc.level.random.nextFloat() < 0.35f) {
            spawn(mc, "world_ambience_wind_gust", 7.0, 1.5, 7.0, 0.01, 0.0, 0.0);
        }

        if (mc.level.isDay() && !mc.level.isRaining() && mc.level.random.nextFloat() < 0.10f) {
            spawn(mc, "world_ambience_pollen", 4.0, 1.0, 4.0, 0.0, 0.01, 0.0);
        }
    }

    private static void spawn(Minecraft mc, String id, double rx, double ry, double rz, double vx, double vy, double vz) {
        var entry = ShineParticles.ALL.get(id);
        if (entry == null || !entry.isPresent()) return;
        double x = mc.player.getX() + (mc.level.random.nextDouble() * 2.0 - 1.0) * rx;
        double y = mc.player.getY() + ry + mc.level.random.nextDouble() * 2.0;
        double z = mc.player.getZ() + (mc.level.random.nextDouble() * 2.0 - 1.0) * rz;
        mc.level.addParticle(entry.get(), x, y, z, vx, vy, vz);
    }
}
