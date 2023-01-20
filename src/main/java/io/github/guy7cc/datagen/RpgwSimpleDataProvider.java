package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.DimensionData;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class RpgwSimpleDataProvider<T> implements DataProvider {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    protected final DataGenerator generator;

    public RpgwSimpleDataProvider(DataGenerator gen){
        generator = gen;
    }

    @Override
    public void run(HashCache pCache){
        HashMap<String, T> map = new HashMap<>();

        register(map);

        for(Map.Entry<String, T> entry : map.entrySet()){
            DataResult<JsonElement> result = getCodec().encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
    }

    protected void save(HashCache cache, JsonObject stateJson, String name){
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + RpgwMod.MOD_ID + "/" + getFolder() + "/" + name + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save " + getNameForLog() + " to {}", outputPath, e);
        }
    }

    protected abstract void register(Map<String, T> map);

    protected abstract Codec<T> getCodec();

    protected abstract String getFolder();

    protected abstract String getNameForLog();
}
