package com.bloom.client.config;

import com.bloom.BloomMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;

/** Small Forge-native config replacing the 26.2/Fabric config stack. */
public final class ShineConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path;

    public static boolean masterEnable = true;
    public static boolean bloom = true;
    public static boolean rimLight = true;
    public static boolean ambientParticles = true;
    public static boolean shading = true;
    public static boolean atmosphere = false;
    public static boolean coloredLighting = false;
    public static boolean waterEffects = true;
    public static boolean weatherEffects = true;
    public static boolean debugMode = false;
    public static float shadingStrength = 0.16f;
    public static float atmosphereStrength = 0.08f;
    public static float bloomStrength = 0.72f;
    public static float bloomThreshold = 0.78f;
    public static float bloomRadius = 1.0f;
    // Sourced from the real original assets/shine/defaults/shine.json (softKnee 0.2, highlightClamp 0.28).
    public static float bloomSoftKnee = 0.2f;
    public static float bloomHighlightClamp = 0.28f;
    public static float rimStrength = 0.48f;
    public static float rimThickness = 1.5f;
    public static float rimDepthThreshold = 0.012f;
    public static int maxRimDistance = 96;

    private ShineConfig() {}

    public static void init() {
        path = FMLPaths.CONFIGDIR.get().resolve("shine.json");
        load();
    }

    public static void load() {
        if (path == null || !Files.exists(path)) {
            save();
            return;
        }
        try {
            JsonObject o = GSON.fromJson(Files.readString(path), JsonObject.class);
            if (o == null) return;
            masterEnable = getBool(o, "masterEnable", masterEnable);
            bloom = getBool(o, "bloom", bloom);
            rimLight = getBool(o, "rimLight", rimLight);
            ambientParticles = getBool(o, "ambientParticles", ambientParticles);
            shading = getBool(o, "shading", shading);
            atmosphere = getBool(o, "atmosphere", atmosphere);
            coloredLighting = getBool(o, "coloredLighting", coloredLighting);
            waterEffects = getBool(o, "waterEffects", waterEffects);
            weatherEffects = getBool(o, "weatherEffects", weatherEffects);
            debugMode = getBool(o, "debugMode", debugMode);
            shadingStrength = getFloat(o, "shadingStrength", shadingStrength, 0f, 1f);
            atmosphereStrength = getFloat(o, "atmosphereStrength", atmosphereStrength, 0f, 1f);
            bloomStrength = getFloat(o, "bloomStrength", bloomStrength, 0f, 3f);
            bloomThreshold = getFloat(o, "bloomThreshold", bloomThreshold, 0f, 2f);
            bloomRadius = getFloat(o, "bloomRadius", bloomRadius, 0.25f, 4f);
            bloomSoftKnee = getFloat(o, "bloomSoftKnee", bloomSoftKnee, 0.01f, 1f);
            bloomHighlightClamp = getFloat(o, "bloomHighlightClamp", bloomHighlightClamp, 0f, 2f);
            rimStrength = getFloat(o, "rimStrength", rimStrength, 0f, 2f);
            rimThickness = getFloat(o, "rimThickness", rimThickness, 0.5f, 5f);
            rimDepthThreshold = getFloat(o, "rimDepthThreshold", rimDepthThreshold, 0.001f, 0.2f);
            maxRimDistance = (int)getFloat(o, "maxRimDistance", maxRimDistance, 8, 256);
        } catch (Exception e) {
            BloomMod.LOGGER.warn("Could not read config; restoring defaults", e);
            save();
        }
    }

    public static void save() {
        if (path == null) return;
        try {
            Files.createDirectories(path.getParent());
            JsonObject o = new JsonObject();
            o.addProperty("masterEnable", masterEnable);
            o.addProperty("bloom", bloom);
            o.addProperty("rimLight", rimLight);
            o.addProperty("ambientParticles", ambientParticles);
            o.addProperty("shading", shading);
            o.addProperty("atmosphere", atmosphere);
            o.addProperty("coloredLighting", coloredLighting);
            o.addProperty("waterEffects", waterEffects);
            o.addProperty("weatherEffects", weatherEffects);
            o.addProperty("debugMode", debugMode);
            o.addProperty("shadingStrength", shadingStrength);
            o.addProperty("atmosphereStrength", atmosphereStrength);
            o.addProperty("bloomStrength", bloomStrength);
            o.addProperty("bloomThreshold", bloomThreshold);
            o.addProperty("bloomRadius", bloomRadius);
            o.addProperty("bloomSoftKnee", bloomSoftKnee);
            o.addProperty("bloomHighlightClamp", bloomHighlightClamp);
            o.addProperty("rimStrength", rimStrength);
            o.addProperty("rimThickness", rimThickness);
            o.addProperty("rimDepthThreshold", rimDepthThreshold);
            o.addProperty("maxRimDistance", maxRimDistance);
            Files.writeString(path, GSON.toJson(o));
        } catch (IOException e) {
            BloomMod.LOGGER.warn("Could not save Shine config", e);
        }
    }

    private static boolean getBool(JsonObject o, String key, boolean def) {
        return o.has(key) ? o.get(key).getAsBoolean() : def;
    }

    private static float getFloat(JsonObject o, String key, float def, float min, float max) {
        if (!o.has(key)) return def;
        return Math.max(min, Math.min(max, o.get(key).getAsFloat()));
    }
}
