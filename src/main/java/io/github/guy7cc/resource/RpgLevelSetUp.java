package io.github.guy7cc.resource;

import com.google.gson.JsonArray;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.util.BlockStateJsonConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;

public abstract class RpgLevelSetUp {
    public static final Codec<RpgLevelSetUp> CODEC = Codec.either(
            SetBlock.CODEC,
            Codec.either(Fill.CODEC, FromNbt.CODEC)
                    .flatComapMap(
                            either -> either.map(Function.identity(), Function.identity()),
                            setUp -> {
                                if(setUp instanceof Fill) return DataResult.success(Either.left((Fill)setUp));
                                if(setUp instanceof FromNbt) return DataResult.success(Either.right((FromNbt)setUp));
                                return DataResult.error("The set up object is neither fill nor fromNbt!");
                            })
    ).flatComapMap(
            either -> either.map(Function.identity(), Function.identity()),
            setUp -> {
                if(setUp instanceof SetBlock) return DataResult.success(Either.left((SetBlock)setUp));
                if(setUp != null) return DataResult.success(Either.right(setUp));
                return DataResult.error("The set up object is null!");
            });

    protected static final Logger LOGGER = LogUtils.getLogger();

    public abstract void perform(MinecraftServer server);

    public static class SetBlock extends RpgLevelSetUp {
        public static final Codec<SetBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(i -> i.dimension),
                ResourceLocation.CODEC.fieldOf("id").forGetter(i -> i.id),
                BlockPos.CODEC.fieldOf("pos").forGetter(i -> i.pos),
                Codec.optionalField("properties", Codec.pair(Codec.STRING.fieldOf("name").codec(), Codec.STRING.fieldOf("value").codec()).listOf()).forGetter(i -> i.properties),
                Codec.optionalField("tag", CompoundTag.CODEC).forGetter(i -> i.tag)
        ).apply(instance, SetBlock::new));

        protected ResourceLocation dimension;
        protected ResourceLocation id;
        protected BlockPos pos;
        protected Optional<List<Pair<String, String>>> properties;
        protected Optional<CompoundTag> tag;

        public SetBlock(ResourceLocation dimension, ResourceLocation id, BlockPos pos, Optional<List<Pair<String, String>>> properties, Optional<CompoundTag> tag){
            this.dimension = dimension;
            this.id = id;
            this.pos = pos;
            this.properties = properties;
            this.tag = tag;
        }

        public SetBlock(ResourceLocation dimension, BlockState state, BlockPos pos, Optional<CompoundTag> tag){
            this.dimension = dimension;
            this.id = state.getBlock().getRegistryName();
            this.pos = pos;
            Collection<Property<?>> collection = state.getProperties();
            if(collection.size() == 0){
                this.properties = Optional.empty();
            } else {
                ArrayList<Pair<String, String>> properties = new ArrayList<>();
                for(Property<?> property : collection){
                    properties.add(Pair.of(property.getName(), state.getValue(property).toString()));
                }
                this.properties = Optional.of(properties);
            }
            this.tag = tag;
        }

        @Override
        public void perform(MinecraftServer server){
            Optional<ResourceKey<Level>> key = server.levelKeys().stream().filter(k -> k.location().equals(dimension)).findFirst();
            if(key.isEmpty()) {
                LOGGER.warn("Cannot get the dimension to place the block: {}", id.toString());
                return;
            }
            ServerLevel level = server.getLevel(key.get());
            Block block = Registry.BLOCK.get(id);
            BlockState state = block.defaultBlockState();
            if(properties.isPresent()){
                List<Pair<String, String>> entries = properties.get();
                for(Pair<String, String> entry : entries){
                    Property<?> property = block.getStateDefinition().getProperty(entry.getFirst());
                    state = BlockStateJsonConverter.setValueHelper(state, property, entry.getFirst(), entry.getSecond());
                }
            }
            level.setBlockAndUpdate(pos, state);
            if(tag.isPresent() && block instanceof EntityBlock eb){
                BlockEntity be = eb.newBlockEntity(pos, state);
                level.setBlockEntity(be);
                be.load(tag.get());
                be.setChanged();
            }
        }
    }

    public static class Fill extends RpgLevelSetUp {
        public static final Codec<Fill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(i -> i.dimension),
                ResourceLocation.CODEC.fieldOf("id").forGetter(i -> i.id),
                BlockPos.CODEC.fieldOf("edge0").forGetter(i -> i.edge0),
                BlockPos.CODEC.fieldOf("edge1").forGetter(i -> i.edge1),
                Codec.optionalField("properties", Codec.pair(Codec.STRING.fieldOf("name").codec(), Codec.STRING.fieldOf("value").codec()).listOf()).forGetter(i -> i.properties),
                Codec.optionalField("tag", CompoundTag.CODEC).forGetter(i -> i.tag)
        ).apply(instance, Fill::new));

        protected ResourceLocation dimension;
        protected ResourceLocation id;
        protected BlockPos edge0;
        protected BlockPos edge1;
        protected Optional<List<Pair<String, String>>> properties;
        protected Optional<CompoundTag> tag;

        public Fill(ResourceLocation dimension, ResourceLocation id, BlockPos edge0, BlockPos edge1, Optional<List<Pair<String, String>>> properties, Optional<CompoundTag> tag){
            this.dimension = dimension;
            this.id = id;
            this.edge0 = edge0;
            this.edge1 = edge1;
            this.properties = properties;
            this.tag = tag;
        }

        @Override
        public void perform(MinecraftServer server) {

        }
    }

    public static class FromNbt extends RpgLevelSetUp {
        public static final Codec<FromNbt> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(i -> i.dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(i -> i.pos),
                Codec.STRING.fieldOf("nbt").forGetter(i -> i.nbt)
        ).apply(instance, FromNbt::new));

        protected ResourceLocation dimension;
        protected BlockPos pos;
        protected String nbt;

        public FromNbt(ResourceLocation dimension, BlockPos pos, String nbt){
            this.dimension = dimension;
            this.pos = pos;
            this.nbt = nbt;
        }

        @Override
        public void perform(MinecraftServer server){

        }
    }
}
