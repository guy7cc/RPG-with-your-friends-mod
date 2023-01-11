package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record RpgScenario(
        String title,
        String info,
        BlockPos spawnPoint,
        ResourceLocation dimension,
        List<RpgScenarioFeature> features,
        List<RpgScenarioCondition> conditions,
        List<RpgScenarioSetUp> setUp,
        List<RpgScenarioReward> initialReward,
        List<RpgScenarioReward> repeatReward) {
    public static final Codec<RpgScenario> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(RpgScenario::title),
            Codec.STRING.fieldOf("info").forGetter(RpgScenario::info),
            BlockPos.CODEC.fieldOf("spawnPoint").forGetter(RpgScenario::spawnPoint),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(RpgScenario::dimension),
            RpgScenarioFeature.CODEC.listOf().fieldOf("features").forGetter(RpgScenario::features),
            RpgScenarioCondition.CODEC.listOf().fieldOf("conditions").forGetter(RpgScenario::conditions),
            RpgScenarioSetUp.CODEC.listOf().fieldOf("setUp").forGetter(RpgScenario::setUp),
            RpgScenarioReward.CODEC.listOf().fieldOf("initialReward").forGetter(RpgScenario::initialReward),
            RpgScenarioReward.CODEC.listOf().fieldOf("repeatReward").forGetter(RpgScenario::repeatReward)
    ).apply(instance, RpgScenario::new));

    public static final RpgScenario DEFAULT = new RpgScenario(
            "rpgscenario.title.default",
            "rpgscenario.info.default",
            new BlockPos(0, 100, 0),
            RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
            List.of(new RpgScenarioFeature.Adventure(true), new RpgScenarioFeature.KeepInventory(true)),
            List.of(),
            List.of(),
            List.of(),
            List.of()
    );
}
