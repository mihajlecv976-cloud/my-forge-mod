package com.bloom;
import com.bloom.client.config.ShineConfig;
import com.bloom.client.ShineParticles;
import com.bloom.client.ShineSounds;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
@Mod(BloomMod.MOD_ID)
public final class BloomMod {
    public static final String MOD_ID = "shine";
    public static final Logger LOGGER = LogUtils.getLogger();
    public BloomMod() {
        ShineParticles.register(FMLJavaModLoadingContext.get().getModEventBus());
        ShineSounds.register();
        ShineConfig.init(); LOGGER.info("Shine 1.20.1 Forge backport bootstrap loaded"); }
}
