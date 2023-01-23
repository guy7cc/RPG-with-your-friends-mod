package io.github.guy7cc.resource;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class RpgScenarioReward {
    public static final Codec<RpgScenarioReward> CODEC = CodecUtil.toParentCodec(RpgScenarioReward.class,
            new CodecUtil.WithType<>(Exp.CODEC, Exp.class),
            new CodecUtil.WithType<>(ExtendMaxExp.CODEC, ExtendMaxExp.class),
            new CodecUtil.WithType<>(Items.CODEC, Items.class)
    );

    protected static final Logger LOGGER = LogManager.getLogger();

    public abstract void give(ServerPlayer player);

    public static class Exp extends RpgScenarioReward {
        public static final Codec<Exp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("exp").forGetter(r -> r.exp)
        ).apply(instance, Exp::new));

        private int exp;

        public Exp(int exp){
            this.exp = exp;
        }

        @Override
        public void give(ServerPlayer player){
            throw new NotImplementedException();
        }
    }

    public static class ExtendMaxExp extends RpgScenarioReward {
        public static final Codec<ExtendMaxExp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("maxExp").forGetter(r -> r.maxExp)
        ).apply(instance, ExtendMaxExp::new));

        private long maxExp;

        public ExtendMaxExp(long maxExp){
            this.maxExp = maxExp;
        }

        @Override
        public void give(ServerPlayer player){
            throw new NotImplementedException();
        }
    }

    public static class Items extends RpgScenarioReward {
        public static final Codec<Items> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.pair(Codec.INT.fieldOf("index").codec(), ItemStack.CODEC.fieldOf("item").codec()).listOf().fieldOf("items").forGetter(r -> r.items)
        ).apply(instance, Items::new));

        private List<Pair<Integer, ItemStack>> items;

        public Items(List<Pair<Integer, ItemStack>> items){
            this.items = items;
        }

        @Override
        public void give(ServerPlayer player){
            throw new NotImplementedException();
        }
    }
}
