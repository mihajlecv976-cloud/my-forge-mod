# Fabric → Forge migration map

- ModInitializer → `@Mod` + Forge event bus
- Fabric client entrypoint → `FMLClientSetupEvent`
- Fabric lifecycle/render events → Forge client/render events + direct renderer hooks
- Fabric resource reload → Forge reload events/listeners
- Fabric registry/particles → DeferredRegister + Forge registries + ParticleProvider
- Fabric networking → Forge SimpleChannel where needed
- ModMenu → Forge/vanilla config screen hook
- Access Widener → Access Transformer and/or Mixin accessor
- Fabric Rendering API → Forge 1.20.1 Blaze3D/renderer hooks
- Sodium 0.9.x → optional Embeddium/Rubidium adapter
- 26.2 RenderPass/GpuBuffer/GpuTexture → 1.20.1 RenderTarget/VertexBuffer/TextureTarget/RenderSystem redesign
- 26.2 renderer-state classes → 1.20.1 LevelRenderer/GameRenderer/RenderType/chunk-render semantic rewrite
