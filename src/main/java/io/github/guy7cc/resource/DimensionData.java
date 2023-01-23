package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public record DimensionData(ResourceLocation key, String title, String subtitle) {
    public static final Codec<DimensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("key").forGetter(DimensionData::key),
            Codec.STRING.fieldOf("title").forGetter(DimensionData::title),
            Codec.STRING.fieldOf("subtitle").forGetter(DimensionData::subtitle)
    ).apply(instance, DimensionData::new));

    public static final DimensionData DEFAULT = new DimensionData(new ResourceLocation(RpgwMod.MOD_ID, "rpgw_debug_dimension"), "Debug World", "Coordinate (∞, 0)");
}
