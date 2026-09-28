package com.bloom.client.render;

import com.bloom.BloomMod;
import com.bloom.client.config.ShineConfig;
import com.bloom.mixin.PostChainAccessor;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge 1.20.1 replacement for Shine's 26.2 GPU/post-effect layer.
 * Uses the stable 1.20.1 PostChain instead of RenderPass/GpuTexture.
 */
@Mod.EventBusSubscriber(modid = BloomMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ShinePostEffects {
    private static final ResourceLocation CHAIN_ID = new ResourceLocation(BloomMod.MOD_ID, "shaders/post/shine.json");
    private static PostChain chain;
    private static int width = -1;
    private static int height = -1;
    private static boolean failed;

    private ShinePostEffects() {}

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused() || failed) return;
        if (!ShineConfig.masterEnable) return;
        if (!ShineConfig.bloom && !ShineConfig.rimLight && !ShineConfig.shading && !ShineConfig.atmosphere) return;

        ensureChain(mc);
        if (chain == null) return;

        int w = mc.getWindow().getWidth();
        int h = mc.getWindow().getHeight();
        if (w <= 0 || h <= 0) return;
        if (w != width || h != height) {
            width = w;
            height = h;
            chain.resize(w, h);
        }

        setUniforms();
        try {
            chain.process(event.getPartialTick());
            mc.getMainRenderTarget().bindWrite(false);
        } catch (Throwable t) {
            failed = true;
            BloomMod.LOGGER.error("Shine post-processing disabled after render error", t);
        }
    }

    private static void ensureChain(Minecraft mc) {
        if (chain != null) return;
        try {
            chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), CHAIN_ID);
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            width = mc.getWindow().getWidth();
            height = mc.getWindow().getHeight();
            BloomMod.LOGGER.info("Shine 1.20.1 post-processing chain loaded");
        } catch (IOException | RuntimeException e) {
            failed = true;
            BloomMod.LOGGER.error("Unable to load Shine post-processing chain", e);
        }
    }

    private static void setUniforms() {
        for (PostPass pass : ((PostChainAccessor) chain).shine$getPasses()) {
            String name = pass.getEffect().getName();
            if (name.endsWith("shine_bloom_extract")) {
                var u = pass.getEffect().getUniform("Threshold");
                if (u != null) u.set(ShineConfig.bloom ? ShineConfig.bloomThreshold : 10f);
                u = pass.getEffect().getUniform("SoftKnee");
                if (u != null) u.set(ShineConfig.bloomSoftKnee);
                u = pass.getEffect().getUniform("HighlightClamp");
                if (u != null) u.set(ShineConfig.bloomHighlightClamp);
            } else if (name.endsWith("shine_bloom_composite")) {
                var u = pass.getEffect().getUniform("Strength");
                if (u != null) u.set(ShineConfig.bloom ? ShineConfig.bloomStrength : 0f);
            } else if (name.endsWith("shine_shading")) {
                var u = pass.getEffect().getUniform("Strength");
                if (u != null) u.set(ShineConfig.shading ? ShineConfig.shadingStrength : 0f);
            } else if (name.endsWith("shine_atmosphere")) {
                var u = pass.getEffect().getUniform("Strength");
                if (u != null) u.set(ShineConfig.atmosphere ? ShineConfig.atmosphereStrength : 0f);
            } else if (name.endsWith("shine_rimlight")) {
                var u = pass.getEffect().getUniform("Strength");
                if (u != null) u.set(ShineConfig.rimLight ? ShineConfig.rimStrength : 0f);
                u = pass.getEffect().getUniform("Thickness");
                if (u != null) u.set(ShineConfig.rimThickness);
                u = pass.getEffect().getUniform("DepthThreshold");
                if (u != null) u.set(ShineConfig.rimDepthThreshold);
                u = pass.getEffect().getUniform("MaxDistance");
                if (u != null) u.set((float) ShineConfig.maxRimDistance);
            }
        }
    }

    public static void close() {
        if (chain != null) {
            chain.close();
            chain = null;
        }
        failed = false;
    }
}
