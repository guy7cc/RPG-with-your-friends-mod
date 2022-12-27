package io.github.guy7cc.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class BlockStateJsonConverter {
    private static final Logger LOGGER = LogManager.getLogger();

    public static JsonObject toJson(BlockState state){
        JsonObject root = new JsonObject();
        root.addProperty("id", state.getBlock().getRegistryName().toString());
        if(!state.getValues().isEmpty()){
            JsonArray values = new JsonArray();
            for(var entry : state.getValues().entrySet()){
                JsonArray v = new JsonArray();
                v.add(entry.getKey().getName());
                v.add(entry.getValue().toString());
                values.add(v);
            }
            root.add("properties", values);
        }
        return root;
    }

    public static JsonObject toJson(ServerLevel level, BlockPos pos){
        BlockState state = level.getBlockState(pos);
        JsonObject root = toJson(state);
        JsonArray jsonPos = new JsonArray();
        jsonPos.add(pos.getX());
        jsonPos.add(pos.getY());
        jsonPos.add(pos.getZ());
        root.add("pos", jsonPos);
        if(state.hasBlockEntity()){
            BlockEntity be = level.getBlockEntity(pos);
            if(be != null){
                CompoundTag tag = be.saveWithoutMetadata();
                JsonElement tagObj = CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, tag).result().orElse(null);
                if(tagObj != null){
                    root.add("tag", tagObj);
                }
            }
        }
        return root;
    }

    public static void setBlockFromJson(JsonObject obj, ServerLevel level){
        if(!obj.has("id")) LOGGER.warn("Cannot determine a block type because of not having the id in Json Object.");
        if(!obj.has("pos")) LOGGER.warn("Cannot determine a block position to place the block.");

        try{
            Block block = Registry.BLOCK.get(new ResourceLocation(obj.get("id").getAsString()));
            BlockPos pos = toBlockPos(obj.getAsJsonArray("pos"));
            BlockState state = block.defaultBlockState();
            if(obj.has("properties")){
                for(var entry : obj.get("properties").getAsJsonArray()){
                    JsonArray v = entry.getAsJsonArray();
                    String key = v.get(0).getAsString();
                    String value = v.get(1).getAsString();
                    Property<?> property = block.getStateDefinition().getProperty(key);
                    state = setValueHelper(state, property, key, value);
                }
            }
            level.setBlockAndUpdate(pos, state);
            if(obj.has("tag") && block instanceof EntityBlock eb){
                CompoundTag tag = CompoundTag.CODEC.parse(JsonOps.INSTANCE, obj.getAsJsonObject("tag")).result().orElse(null);
                if(tag != null){
                    BlockEntity be = eb.newBlockEntity(pos, state);
                    level.setBlockEntity(be);
                    be.load(tag);
                    be.setChanged();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static BlockPos toBlockPos(JsonArray array){
        int[] arr = new int[3];
        int i = 0;
        for(JsonElement je : array){
            arr[i] = je.getAsInt();
            i++;
        }
        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    public static <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<T> property, String key, String value) {
        Optional<T> optional = property.getValue(value);
        if (optional.isPresent()) {
            return state.setValue(property, optional.get());
        } else {
            LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, value, state.getBlock().getRegistryName().toString());
            return state;
        }
    }
}
