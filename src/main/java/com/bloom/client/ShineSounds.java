package com.bloom.client;

import com.bloom.BloomMod;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Registers the sound-event ids declared by Shine's sounds.json. */
public final class ShineSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, BloomMod.MOD_ID);
    public static final Map<String, RegistryObject<SoundEvent>> ALL = new LinkedHashMap<>();
    private static final String[] IDS = {
        "leaf_litter_contact",
        "leaf_litter_walk",
        "leaf_litter_rustle",
        "leaf_litter_slide",
        "leaf_litter_land",
        "tumbleweed_hit_01",
        "tumbleweed_hit_02",
        "tumbleweed_hit_03",
        "tumbleweed_hit_04",
        "tumbleweed_punch_01",
        "tumbleweed_punch_02",
        "lava_pop_01",
        "lava_pop_02",
        "lava_pop_03",
        "lava_spray_01",
        "lava_spray_02",
        "lava_droplet_01",
        "lava_droplet_02",
        "falling_leaf_water",
        "water_cascade_loop",
        "branch_litter_contact",
        "pebble_litter_contact",
        "weather_drizzle",
        "weather_light_rain",
        "weather_desert_wind",
        "weather_nether_storm",
        "psychedelic_hallucination",
        "firefly_bush_ambient",
        "cicada_ambient",
        "deep_dark_ambient",
        "bird_continuous",
        "bird_call_01",
        "bird_call_02",
        "bird_call_03",
        "bird_call_04",
        "bird_call_05",
        "bird_call_06",
        "bird_call_07",
        "bird_flyaway",
        "geese_continuous",
        "geese_flock_call_01",
        "geese_flock_call_02",
        "geese_flock_call_03",
    };
    static {
        for (String id : IDS) ALL.put(id, SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BloomMod.MOD_ID, id))));
    }
    private ShineSounds() {}
    public static void register() { SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus()); }
}
