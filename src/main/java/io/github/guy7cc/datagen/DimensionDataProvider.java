package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.DimensionData;
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
import java.util.Map;

public class DimensionDataProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public DimensionDataProvider(DataGenerator gen, ExistingFileHelper exFileHelper){
        generator = gen;
    }

    @Override
    public void run(HashCache pCache){
        HashMap<String, DimensionData> map = new HashMap<>();

        register(map);

        for(Map.Entry<String, DimensionData> entry : map.entrySet()){
            JsonObject json = (JsonObject)DimensionData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().get();
            save(pCache, json, "rpgdata/dimension", entry.getKey());
        }
    }

    private void register(Map<String, DimensionData> map){
        map.put("test", new DimensionData(RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(), "Debug World", "Coordinate (∞, 0)"));
    }

    public void save(HashCache cache, JsonObject stateJson, String folder, String name){
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + RpgwMod.MOD_ID + "/" + folder + "/" + name + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save dimension data to {}", outputPath, e);
        }
    }

    @Override
    public String getName() {
        return "Dimension Data: " + RpgwMod.MOD_ID;
    }
}
