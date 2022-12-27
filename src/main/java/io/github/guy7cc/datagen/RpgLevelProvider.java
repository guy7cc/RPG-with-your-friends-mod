package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.RpgLevel;
import io.github.guy7cc.resource.RpgLevelSetUp;
import io.github.guy7cc.resource.RpgStage;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RpgLevelProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public RpgLevelProvider(DataGenerator gen, ExistingFileHelper exFileHelper){
        generator = gen;
    }

    @Override
    public void run(HashCache pCache){
        HashMap<String, RpgLevel> map = new HashMap<>();

        register(map);

        for(Map.Entry<String, RpgLevel> entry : map.entrySet()){
            DataResult<JsonElement> result = RpgLevel.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
    }

    private void register(Map<String, RpgLevel> map){
        map.put("test_level", new RpgLevel(
                "Debug Level",
                "No Info",
                new BlockPos(0, 100, 0),
                RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
                List.of(
                        new RpgLevelSetUp.SetBlock(
                                RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
                                Blocks.STONE_BRICK_SLAB.defaultBlockState(),
                                new BlockPos(0, 99, 0),
                                Optional.empty()
                        )
                )));
    }

    public void save(HashCache cache, JsonObject stateJson, String name){
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + RpgwMod.MOD_ID + "/rpgdata/level/" + name + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save rpg level to {}", outputPath, e);
        }
    }

    @Override
    public String getName() {
        return "Rpg Level: " + RpgwMod.MOD_ID;
    }
}
