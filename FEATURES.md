# Backport feature matrix

| Feature family | 1.20.1 Forge status |
|---|---|
| Selective bloom | Implemented with native PostChain |
| Rim lighting | Implemented with main-depth edge detection |
| Shine config | Implemented (`config/shine.json`) |
| Custom particle ids | Registered with generic sprite fallback |
| Shine sound ids | Registered from the shipped `sounds.json` |
| Shine textures / particle JSON | Retained |
| 26.2 RenderPass/GpuBuffer/GpuTexture code | Replaced, not copied |
| 26.2 Sodium renderer hooks | Not copied; incompatible by design |
| 26.2 colored-light chunk propagation | Not safely portable as a drop-in without a 1.20.1 light-engine/chunk renderer integration |
| 26.2-only blocks/biomes | Not registered as gameplay content |
| Vulkan-specific paths | Not used |
