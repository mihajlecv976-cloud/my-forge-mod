# Shine 3.1.1+26.2 -> Minecraft 1.20.1 Forge 47.4.20

This tree is the completed source-side backport implementation currently possible without a local Minecraft/Forge dependency cache.

## Implemented
- Forge Java 17 bootstrap (`@Mod`, no Fabric Loader entrypoints).
- Forge config at `config/shine.json`.
- Real 1.20.1 `PostChain` execution at `RenderLevelStageEvent.Stage.AFTER_LEVEL`.
- Selective bloom: threshold -> horizontal blur -> vertical blur -> composite.
- Depth-based screen-space rim lighting.
- Generic compatibility registrations for Shine's custom particle ids.
- Original Shine texture/audio/particle JSON payload retained where compatible.
- 1.20.1 classic shader layout (`shaders/post` + `shaders/program`).
- No dependency on Fabric API, Sodium 0.9, or 26.2 GPU classes.

## Deliberately replaced
The original 26.2 renderer uses RenderPass/GpuTexture/GpuBuffer/renderer-state APIs that do not exist in 1.20.1. Those are not shimmed with fake classes. The backport uses the native 1.20.1 post-processing path instead.

## Still version-limited
Features that depend directly on 26.2 chunk renderer internals, colored block-light propagation, or 26.2-only blocks/biomes need dedicated 1.20.1 renderer hooks. Their resource payload is retained but modern-only content is not registered as gameplay content.

## Build requirement
Run `gradlew.bat build` on a machine with network access so ForgeGradle can resolve Minecraft 1.20.1 and Forge 47.4.20. The current sandbox has no Forge/Minecraft dependency cache and cannot honestly produce a tested binary here.
