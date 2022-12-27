package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.DimensionData;
import io.github.guy7cc.resource.RpgLevel;
import io.github.guy7cc.resource.RpgStage;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgStageProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public RpgStageProvider(DataGenerator gen, ExistingFileHelper exFileHelper){
        generator = gen;
    }

    @Override
    public void run(HashCache pCache){
        HashMap<String, RpgStage> map = new HashMap<>();

        register(map);

        for(Map.Entry<String, RpgStage> entry : map.entrySet()){
            DataResult<JsonElement> result = RpgStage.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
    }

    private void register(Map<String, RpgStage> map){
        map.put("test_stage", new RpgStage("Debug Stage", List.of("test_level")));
    }

    public void save(HashCache cache, JsonObject stateJson, String name){
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + RpgwMod.MOD_ID + "/rpgdata/stage/" + name + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save rpg stage to {}", outputPath, e);
        }
    }

    @Override
    public String getName() {
        return "Rpg Stage: " + RpgwMod.MOD_ID;
    }
}
