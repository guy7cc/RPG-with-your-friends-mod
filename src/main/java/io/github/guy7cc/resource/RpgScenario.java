package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            new TranslatableComponent("rpgscenario.title.default"),
            new TranslatableComponent("rpgscenario.info.default"),
            new BlockPos(0, 100, 0),
            RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
            List.of(new RpgScenarioFeature.Adventure(true), new RpgScenarioFeature.KeepInventory(true)),
            List.of(),
            List.of(),
            List.of(),
            List.of()
    );

    public RpgScenario(Component title,
                       Component info,
                       BlockPos spawnPoint,
                       ResourceLocation dimension,
                       List<RpgScenarioFeature> features,
                       List<RpgScenarioCondition> conditions,
                       List<RpgScenarioSetUp> setUp,
                       List<RpgScenarioReward> initialReward,
                       List<RpgScenarioReward> repeatReward){
        this(Component.Serializer.toJson(title), Component.Serializer.toJson(info), spawnPoint, dimension, features, conditions, setUp, initialReward, repeatReward);
    }

    public RpgScenario(RpgScenario clone){
        this(clone.title, clone.info, new BlockPos(clone.spawnPoint), new ResourceLocation(clone.dimension.toString()), new ArrayList<>(clone.features), new ArrayList<>(clone.conditions), new ArrayList<>(clone.setUp), new ArrayList<>(clone.initialReward), new ArrayList<>(clone.repeatReward));
    }

    public RpgScenario reduceSetUp(){
        RpgScenario clone = new RpgScenario(this);
        clone.setUp.clear();
        return clone;
    }
}
