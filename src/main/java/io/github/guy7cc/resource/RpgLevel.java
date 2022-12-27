package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RpgLevel(String title, String info, BlockPos spawnPoint, ResourceLocation dimension, List<RpgLevelSetUp> setUp) {
    public static final Codec<RpgLevel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(RpgLevel::title),
            Codec.STRING.fieldOf("info").forGetter(RpgLevel::info),
            BlockPos.CODEC.fieldOf("spawnPoint").forGetter(RpgLevel::spawnPoint),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(RpgLevel::dimension),
            RpgLevelSetUp.CODEC.listOf().fieldOf("setUp").forGetter(RpgLevel::setUp)
    ).apply(instance, RpgLevel::new));
}
