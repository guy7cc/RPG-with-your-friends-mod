package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.save.RpgSavedData;
import io.github.guy7cc.save.RpgScenarioSavedData;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.List;

public abstract class RpgScenarioCondition {
    public static final Codec<RpgScenarioCondition> CODEC = CodecUtil.toParentCodec(RpgScenarioCondition.class,
            new CodecUtil.WithType<>(MaxPlayer.CODEC, MaxPlayer.class),
            new CodecUtil.WithType<>(PassedScenario.CODEC, PassedScenario.class),
            new CodecUtil.WithType<>(AllowedItems.CODEC, AllowedItems.class),
            new CodecUtil.WithType<>(BannedItems.CODEC, BannedItems.class)
    );

    // Optional.empty() means passing the test
    public abstract Optional<Component> test(Party party);

    public static class MaxPlayer extends RpgScenarioCondition{
        public static final Codec<MaxPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max").forGetter(i -> i.max)
        ).apply(instance, MaxPlayer::new));

        private final int max;

        public MaxPlayer(int max){
            this.max = max;
        }

        public int getMax() {
            return max;
        }

        @Override
        public Optional<Component> test(Party party){
            if(party.isClientSide()) return RpgScenarioConditionExecutor.test(this);
            else {
                if(party.size() <= max) return Optional.empty();
                else return Optional.of(new TranslatableComponent("rpgscenario.condition.maxPlayer.failed"));
            }
        }
    }

    public static class PassedScenario extends RpgScenarioCondition{
        public static final Codec<PassedScenario> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("scenario").forGetter(i -> i.list)
        ).apply(instance, PassedScenario::new));

        private ImmutableList<ResourceLocation> list;
        private Component requirement;

        public PassedScenario(List<ResourceLocation> scenario){
            this.list = ImmutableList.copyOf(scenario);
            MutableComponent component = new TranslatableComponent("rpgscenario.condition.passedScenario.require")
                    .append("\n ")
                    .append(Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(list.get(0)).title()));
            for(int i = 1; i < list.size(); i++){
                component.append("\n ").append(Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(list.get(i)).title()));
            }
            requirement = component;
        }

        public Component getRequirement() {
            return requirement;
        }

        @Override
        public Optional<Component> test(Party party){
            if(party.isClientSide()) return RpgScenarioConditionExecutor.test(this);

            MinecraftServer server = party.getServer();
            RpgSavedData savedData = RpgSavedData.get(server);
            MutableComponent component = null;
            for(ResourceLocation scenarioLoc : list){
                RpgScenarioSavedData scenarioSavedData = savedData.getOrCreateRpgScenarioSavedData(scenarioLoc);
                List<Component> neverPassedNames = new ArrayList<>();
                for(ServerPlayer player : party.getPlayers()){
                    if(!scenarioSavedData.passed(player)) neverPassedNames.add(player.getName());
                }
                if(neverPassedNames.size() > 0){
                    MutableComponent names = new TextComponent("").append(neverPassedNames.get(0));
                    for(int i = 1; i < neverPassedNames.size(); i++){
                        names.append(", ").append(neverPassedNames.get(i));
                    }
                    Component scenarioName = Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(scenarioLoc).title());
                    MutableComponent hasNeverPassedMessage = new TranslatableComponent("rpgscenario.condition.passedScenario.neverPassed", names).append(scenarioName);
                    if(component == null) component = hasNeverPassedMessage;
                    else component.append("\n").append(hasNeverPassedMessage);
                }
            }
            return component == null ? Optional.empty() : Optional.of(component);
        }
    }

    public static class AllowedItems extends RpgScenarioCondition{
        public static final Codec<AllowedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("allowed").forGetter(i -> i.list)
        ).apply(instance, AllowedItems::new));

        private final ImmutableList<ItemStack> list;

        public AllowedItems(List<ItemStack> list){
            this.list = ImmutableList.copyOf(list);
        }

        public ImmutableList<ItemStack> getList() {
            return list;
        }

        @Override
        public Optional<Component> test(Party party){
            if(!party.isClientSide()) return Optional.empty();
            return RpgScenarioConditionExecutor.test(this);
        }
    }

    public static class BannedItems extends RpgScenarioCondition{
        public static final Codec<BannedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("banned").forGetter(i -> i.list)
        ).apply(instance, BannedItems::new));

        private final ImmutableList<ItemStack> list;

        public BannedItems(List<ItemStack> list){
            this.list = ImmutableList.copyOf(list);
        }

        public ImmutableList<ItemStack> getList() {
            return list;
        }

        @Override
        public Optional<Component> test(Party party){
            if(!party.isClientSide()) return Optional.empty();
            return RpgScenarioConditionExecutor.test(this);
        }
    }
}
