package com.bloom.mixin;

import java.util.List;
import net.minecraft.client.renderer.PostPass;

public interface PostChainAccessor {
    List<PostPass> shine$getPasses();
}
