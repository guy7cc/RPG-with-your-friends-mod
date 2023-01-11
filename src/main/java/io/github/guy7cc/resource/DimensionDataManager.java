package io.github.guy7cc.resource;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;

public class DimensionDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DimensionDataManager instance = new DimensionDataManager();

    private Map<ResourceLocation, DimensionData> dataList = new HashMap<>();

    private DimensionDataManager() {
        super(GSON, "rpgdata/dimension");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        dataList.clear();
        dataList.put(new ResourceLocation(RpgwMod.MOD_ID, "default"), DimensionData.DEFAULT);
        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()){
            ResourceLocation resourcelocation = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                DimensionData data = DimensionData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                dataList.put(data.key(), data);
            } catch (IllegalArgumentException | JsonParseException | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading rpgw dimension data {}", resourcelocation, exception);
            }
        }
    }

    public DimensionData get(ResourceLocation dimLoc){
        return dataList.get(dimLoc);
    }
}
