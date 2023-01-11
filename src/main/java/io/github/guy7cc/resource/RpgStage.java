package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record RpgStage(String title, List<String> scenarios) {
    public static final Codec<RpgStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(RpgStage::title),
            Codec.STRING.listOf().fieldOf("scenarios").forGetter(RpgStage::scenarios)
    ).apply(instance, RpgStage::new));

    public static final RpgStage DEFAULT = new RpgStage("rpgstage.title.default",
            List.of("default"));
}
