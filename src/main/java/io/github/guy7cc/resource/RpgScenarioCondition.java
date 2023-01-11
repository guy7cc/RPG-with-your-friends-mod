package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.rpg.Party;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class RpgScenarioCondition {
    public static final Codec<RpgScenarioCondition> CODEC = CodecUtil.toParentCodec(RpgScenarioCondition.class, List.of(
            new CodecWithType<>(MaxPlayer.CODEC, MaxPlayer.class),
            new CodecWithType<>(PassedScenario.CODEC, PassedScenario.class),
            new CodecWithType<>(AllowedItems.CODEC, AllowedItems.class),
            new CodecWithType<>(BannedItems.CODEC, BannedItems.class)
    ));

    public abstract Collection<ServerPlayer> test(Party party);

    public abstract Component getMessage(ServerPlayer player);

    public static class MaxPlayer extends RpgScenarioCondition{
        public static final Codec<MaxPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max").forGetter(i -> i.max)
        ).apply(instance, MaxPlayer::new));

        private int max;

        public MaxPlayer(int max){
            this.max = max;
        }

        @Override
        public Collection<ServerPlayer> test(Party party){
            throw new NotImplementedException();
        }

        @Override
        public Component getMessage(ServerPlayer player){
            throw new NotImplementedException();
        }
    }

    public static class PassedScenario extends RpgScenarioCondition{
        public static final Codec<PassedScenario> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("scenario").forGetter(i -> i.scenario)
        ).apply(instance, PassedScenario::new));

        private List<String> scenario;

        public PassedScenario(List<String> scenario){
            this.scenario = scenario;
        }

        @Override
        public Collection<ServerPlayer> test(Party party){
            throw new NotImplementedException();
        }

        @Override
        public Component getMessage(ServerPlayer player){
            throw new NotImplementedException();
        }
    }

    public static class AllowedItems extends RpgScenarioCondition{
        public static final Codec<AllowedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("allowed").forGetter(i -> i.list)
        ).apply(instance, AllowedItems::new));

        private List<ItemStack> list;

        public AllowedItems(List<ItemStack> list){
            this.list = list;
        }

        @Override
        public Collection<ServerPlayer> test(Party party){
            throw new NotImplementedException();
        }

        @Override
        public Component getMessage(ServerPlayer player){
            throw new NotImplementedException();
        }
    }

    public static class BannedItems extends RpgScenarioCondition{
        public static final Codec<BannedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("banned").forGetter(i -> i.list)
        ).apply(instance, BannedItems::new));

        private List<ItemStack> list;

        public BannedItems(List<ItemStack> list){
            this.list = list;
        }

        @Override
        public Collection<ServerPlayer> test(Party party){
            throw new NotImplementedException();
        }

        @Override
        public Component getMessage(ServerPlayer player){
            throw new NotImplementedException();
        }
    }
}
