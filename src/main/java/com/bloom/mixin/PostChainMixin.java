package com.bloom.mixin;

import java.util.List;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PostChain.class)
public abstract class PostChainMixin implements PostChainAccessor {
    @Shadow private List<PostPass> passes;
    @Override public List<PostPass> shine$getPasses() { return passes; }
}
