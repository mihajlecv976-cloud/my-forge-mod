package com.bloom.client;

import com.bloom.BloomMod;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers Shine's custom particle ids and gives each behavioural family
 * (leaf, pollen, mist/fog, ember/spark, ash, bubble/splash, wind gust,
 * generic ambient) its own {@link TextureSheetParticle} subclass instead of
 * one shared heuristic implementation. This is an APPROXIMATION of the
 * original 26.2 renderer-specific particle systems (which used dedicated
 * GPU render passes per family, see shaders/core/*_particle.fsh) using the
 * stock 1.20.1 sprite-particle pipeline.
 */
@Mod.EventBusSubscriber(modid = BloomMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ShineParticles {
    public static final DeferredRegister<ParticleType<?>> TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, BloomMod.MOD_ID);

    /** Behavioural family a particle id belongs to. */
    private enum Family {
        LEAF, POLLEN, MIST, EMBER, SPARK, ASH, BUBBLE, SPLASH, GUST, AMBIENT_BIRD, GENERIC
    }

    private static Family classify(String id) {
        if (id.contains("leaf") || id.contains("petal") || id.contains("litter")) return Family.LEAF;
        if (id.contains("pollen")) return Family.POLLEN;
        if (id.contains("mist") || id.contains("fog") || id.contains("haze") || id.contains("dust")) return Family.MIST;
        if (id.contains("ember") || id.contains("lava") || id.contains("torch_spark")) return Family.EMBER;
        if (id.contains("spark") || id.contains("sparkle")) return Family.SPARK;
        if (id.contains("ash") || id.contains("smoke") || id.contains("steam")) return Family.ASH;
        if (id.contains("bubble") || id.contains("jellyfish")) return Family.BUBBLE;
        if (id.contains("splash") || id.contains("rain") || id.contains("puddle") || id.contains("wake") || id.contains("foam") || id.contains("cascade")) return Family.SPLASH;
        if (id.contains("gust") || id.contains("wind") || id.contains("tumbleweed")) return Family.GUST;
        if (id.contains("bird") || id.contains("goose") || id.contains("butterfly") || id.contains("firefly")) return Family.AMBIENT_BIRD;
        return Family.GENERIC;
    }

    private static final String[] IDS = {
        "amethyst_sparkle", "beach_pebble_litter", "block_side_rain", "boat_splash", "boat_trail_foam",
        "boat_trail_wake", "branch_litter", "cave_dust", "copper_lantern_mote", "copper_torch_spark",
        "custom_rain", "custom_rain_splash", "desert_dust", "duckweed_litter", "end_portal_eye_placement",
        "flower_litter", "fog_fx", "glowing_ash", "ground_mist", "jellyfish", "lantern_mote",
        "lava_droplet_splash", "lava_ember", "lava_plate", "lava_pop_big", "lava_pop_small", "lava_pop_snappy",
        "lava_spray", "lava_spray_flash", "lava_steam", "leaf_litter", "leaf_litter_kickup", "lily_pad_litter",
        "mist", "nether_rays", "pebble_litter", "petal_litter", "rain_puddle", "shell_litter",
        "soul_lantern_mote", "soul_torch_spark", "torch_smoke", "torch_spark", "trailer_splash_band",
        "trailer_splash_low", "trailer_splash_middle", "trailer_splash_outer", "underwater_chest_bubble",
        "underwater_ender_chest_bubble", "water_cascade", "water_splash_droplet", "weather_particle",
        "world_ambience_bird", "world_ambience_butterfly", "world_ambience_distant_light_ray",
        "world_ambience_falling_acacia_leaf", "world_ambience_falling_azalea_leaf", "world_ambience_falling_birch_leaf",
        "world_ambience_falling_cherry_leaf", "world_ambience_falling_chorus_petal", "world_ambience_falling_jungle_leaf",
        "world_ambience_falling_leaf", "world_ambience_falling_mangrove_leaf", "world_ambience_falling_pale_oak_leaf",
        "world_ambience_falling_spruce_leaf", "world_ambience_falling_tinted_leaf", "world_ambience_firefly",
        "world_ambience_goose", "world_ambience_leaf", "world_ambience_leaf_gust", "world_ambience_leaf_water_splash",
        "world_ambience_lens_flare", "world_ambience_light_ray", "world_ambience_passive_acacia_leaf",
        "world_ambience_passive_azalea_leaf", "world_ambience_passive_birch_leaf", "world_ambience_passive_cherry_leaf",
        "world_ambience_passive_jungle_leaf", "world_ambience_passive_mangrove_leaf", "world_ambience_passive_pale_oak_leaf",
        "world_ambience_passive_spruce_leaf", "world_ambience_pollen", "world_ambience_tumbleweed",
        "world_ambience_water_pollen", "world_ambience_wind_gust",
    };

    public static final Map<String, RegistryObject<SimpleParticleType>> ALL = new LinkedHashMap<>();
    private static final Map<String, Family> FAMILY_OF = new LinkedHashMap<>();
    static {
        for (String id : IDS) {
            ALL.put(id, TYPES.register(id, () -> new SimpleParticleType(true)));
            FAMILY_OF.put(id, classify(id));
        }
    }

    private ShineParticles() {}

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }

    @SubscribeEvent
    public static void providers(RegisterParticleProvidersEvent event) {
        for (String id : IDS) {
            Family family = FAMILY_OF.get(id);
            RegistryObject<SimpleParticleType> entry = ALL.get(id);
            event.registerSpriteSet(entry.get(), sprites -> providerFor(family, sprites));
        }
    }

    private static ParticleProvider<SimpleParticleType> providerFor(Family family, SpriteSet sprites) {
        switch (family) {
            case LEAF: return (type, level, x, y, z, xd, yd, zd) -> new LeafParticle(level, x, y, z, xd, yd, zd, sprites);
            case POLLEN: return (type, level, x, y, z, xd, yd, zd) -> new PollenParticle(level, x, y, z, xd, yd, zd, sprites);
            case MIST: return (type, level, x, y, z, xd, yd, zd) -> new MistParticle(level, x, y, z, xd, yd, zd, sprites);
            case EMBER: return (type, level, x, y, z, xd, yd, zd) -> new EmberParticle(level, x, y, z, xd, yd, zd, sprites);
            case SPARK: return (type, level, x, y, z, xd, yd, zd) -> new SparkParticle(level, x, y, z, xd, yd, zd, sprites);
            case ASH: return (type, level, x, y, z, xd, yd, zd) -> new AshParticle(level, x, y, z, xd, yd, zd, sprites);
            case BUBBLE: return (type, level, x, y, z, xd, yd, zd) -> new BubbleParticle(level, x, y, z, xd, yd, zd, sprites);
            case SPLASH: return (type, level, x, y, z, xd, yd, zd) -> new SplashParticle(level, x, y, z, xd, yd, zd, sprites);
            case GUST: return (type, level, x, y, z, xd, yd, zd) -> new GustParticle(level, x, y, z, xd, yd, zd, sprites);
            case AMBIENT_BIRD: return (type, level, x, y, z, xd, yd, zd) -> new AmbientDriftParticle(level, x, y, z, xd, yd, zd, sprites);
            default: return (type, level, x, y, z, xd, yd, zd) -> new GenericParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }

    /** Shared setup: fade in/out over lifetime, alpha blended sprite sheet. */
    private abstract static class ShineParticleBase extends TextureSheetParticle {
        protected final float baseScale;
        protected ShineParticleBase(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd);
            this.setSpriteFromAge(sprites);
            this.baseScale = this.quadSize;
        }
        @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        protected float fadeAlpha() {
            float life = 1.0f - (float) age / Math.max(1, lifetime);
            int fadeIn = Math.min(5, lifetime / 4);
            float in = fadeIn > 0 && age < fadeIn ? (float) age / fadeIn : 1.0f;
            return Math.max(0.0f, Math.min(1.0f, life * in));
        }
    }

    /** Falling/drifting foliage: gentle sway, slow downward drift, long life. */
    private static final class LeafParticle extends ShineParticleBase {
        LeafParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0.04f;
            this.friction = 0.98f;
            this.lifetime = 60 + this.random.nextInt(90);
            this.quadSize = 0.05f + this.random.nextFloat() * 0.05f;
            this.xd *= 0.15f; this.yd *= 0.15f; this.zd *= 0.15f;
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += Math.sin((age + x * 3.0) * 0.08) * 0.003;
            zd += Math.cos((age + z * 2.0) * 0.07) * 0.003;
            yd -= gravity * 0.02f;
            xd *= friction; yd *= friction; zd *= friction;
            move(xd, yd, zd);
            if (onGround || age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Slow-rising drifting motes, brighter, very light gravity. */
    private static final class PollenParticle extends ShineParticleBase {
        PollenParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = -0.01f;
            this.friction = 0.99f;
            this.lifetime = 50 + this.random.nextInt(40);
            this.quadSize = 0.035f + this.random.nextFloat() * 0.03f;
            this.setColor(0.95f, 0.9f, 0.55f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += (random.nextFloat() - 0.5f) * 0.004;
            zd += (random.nextFloat() - 0.5f) * 0.004;
            yd -= gravity;
            xd *= friction; yd *= friction; zd *= friction;
            move(xd, yd, zd);
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha() * 0.85f);
        }
    }

    /** Large, slow, soft, near-stationary volumetric puffs. */
    private static final class MistParticle extends ShineParticleBase {
        MistParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0f;
            this.friction = 0.94f;
            this.lifetime = 40 + this.random.nextInt(50);
            this.quadSize = (0.18f + this.random.nextFloat() * 0.14f);
            this.xd *= 0.06f; this.yd *= 0.02f; this.zd *= 0.06f;
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha() * 0.55f);
            quadSize = baseScale * (1.0f + 0.35f * (float) age / lifetime);
        }
    }

    /** Hot glowing particle with rising heat shimmer and warm color, short life. */
    private static final class EmberParticle extends ShineParticleBase {
        EmberParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = -0.02f;
            this.friction = 0.96f;
            this.lifetime = 15 + this.random.nextInt(20);
            this.quadSize = 0.04f + this.random.nextFloat() * 0.04f;
            this.xd *= 0.5f; this.yd *= 0.5f; this.zd *= 0.5f;
            this.setColor(1.0f, 0.68f + random.nextFloat() * 0.2f, 0.22f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            yd -= gravity;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Bright, fast, straight-line flashes (sparkle, spark). */
    private static final class SparkParticle extends ShineParticleBase {
        SparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0.02f;
            this.friction = 0.9f;
            this.lifetime = 8 + this.random.nextInt(12);
            this.quadSize = 0.03f + this.random.nextFloat() * 0.02f;
            this.setColor(1.0f, 1.0f, 0.85f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            yd -= gravity * 0.05f;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Floaty, tumbling, dark smoke/ash flecks, medium life. */
    private static final class AshParticle extends ShineParticleBase {
        AshParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = -0.015f;
            this.friction = 0.97f;
            this.lifetime = 30 + this.random.nextInt(35);
            this.quadSize = 0.05f + this.random.nextFloat() * 0.05f;
            this.setColor(0.5f, 0.5f, 0.5f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += Math.sin(age * 0.1) * 0.002;
            yd -= gravity;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha() * 0.7f);
        }
    }

    /** Rises steadily, gentle wobble, pops near top of its life. */
    private static final class BubbleParticle extends ShineParticleBase {
        BubbleParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = -0.04f;
            this.friction = 0.95f;
            this.lifetime = 12 + this.random.nextInt(18);
            this.quadSize = 0.04f + this.random.nextFloat() * 0.03f;
            this.setColor(0.75f, 0.9f, 1.0f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += Math.sin(age * 0.3) * 0.002;
            yd -= gravity;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Short, fast, outward burst then quick fall/settle (rain/water impact). */
    private static final class SplashParticle extends ShineParticleBase {
        SplashParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0.06f;
            this.friction = 0.9f;
            this.lifetime = 6 + this.random.nextInt(10);
            this.quadSize = 0.045f + this.random.nextFloat() * 0.03f;
            this.setColor(0.75f, 0.85f, 1.0f);
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            yd -= gravity;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime || onGround) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Fast horizontal streaks with no gravity (wind/tumbleweed gust). */
    private static final class GustParticle extends ShineParticleBase {
        GustParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0f;
            this.friction = 0.98f;
            this.lifetime = 20 + this.random.nextInt(20);
            this.quadSize = 0.06f + this.random.nextFloat() * 0.05f;
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += Math.sin(age * 0.16) * 0.004;
            zd += Math.cos(age * 0.13) * 0.004;
            move(xd, yd, zd);
            xd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha() * 0.6f);
        }
    }

    /** Slow, wide, looping drift for distant ambient wildlife/light-ray effects. */
    private static final class AmbientDriftParticle extends ShineParticleBase {
        AmbientDriftParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0f;
            this.friction = 0.99f;
            this.lifetime = 70 + this.random.nextInt(60);
            this.quadSize = 0.05f + this.random.nextFloat() * 0.04f;
            this.xd *= 0.2f; this.yd *= 0.05f; this.zd *= 0.2f;
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            xd += Math.sin(age * 0.05) * 0.0015;
            zd += Math.cos(age * 0.04) * 0.0015;
            move(xd, yd, zd);
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }

    /** Fallback for any id that doesn't match a specific family. */
    private static final class GenericParticle extends ShineParticleBase {
        GenericParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
            super(level, x, y, z, xd, yd, zd, sprites);
            this.gravity = 0.02f;
            this.friction = 0.96f;
            this.lifetime = 20 + this.random.nextInt(20);
            this.quadSize = 0.05f + this.random.nextFloat() * 0.04f;
        }
        @Override public void tick() {
            xo = x; yo = y; zo = z;
            yd -= gravity;
            move(xd, yd, zd);
            xd *= friction; yd *= friction; zd *= friction;
            if (age++ >= lifetime) remove();
            setAlpha(fadeAlpha());
        }
    }
}
