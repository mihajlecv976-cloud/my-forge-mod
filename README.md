# Shine 3.1.1+26.2 — Forge 1.20.1 backport tree

Target: Minecraft 1.20.1 / Forge 47.4.20 / Java 17.

This project is a real source-level port, not a repackaged Fabric JAR. The 26.2 GPU renderer was replaced with the 1.20.1 `PostChain` path. The resource payload from the supplied Shine JAR is retained, while modern-only gameplay content is not registered.

### Main implemented systems
- selective bloom
- depth-based rim light
- Forge config
- 85 Shine particle ids with JSON sprite providers
- 43 Shine sound event ids
- original Shine assets retained
- no active Fabric imports
- no Sodium 0.9 dependency

### Build
See `BUILD.md`. A binary build cannot be verified in this sandbox because Minecraft/Forge artifacts are not locally cached and the sandbox cannot resolve external Maven hosts.
