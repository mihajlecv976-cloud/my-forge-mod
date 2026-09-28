package com.bloom.client;

import com.bloom.BloomMod;
import com.bloom.client.config.ShineConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BloomMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BloomClient {
    private BloomClient() {}
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ShineConfig::load);
        BloomMod.LOGGER.info("Shine 1.20.1 Forge renderer bootstrap initialized");
    }
}
