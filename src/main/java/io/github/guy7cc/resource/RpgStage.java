package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.RpgwMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RpgStage(String title, List<ResourceLocation> scenarios) {
    public static final Codec<RpgStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(RpgStage::title),
            ResourceLocation.CODEC.listOf().fieldOf("scenarios").forGetter(RpgStage::scenarios)
    ).apply(instance, RpgStage::new));

    public static final RpgStage DEFAULT = new RpgStage(new TranslatableComponent("rpgstage.title.default"),
            List.of(new ResourceLocation(RpgwMod.MOD_ID, "default")));

    public RpgStage(Component title, List<ResourceLocation> scenarios){
        this(Component.Serializer.toJson(title), scenarios);
    }
}
