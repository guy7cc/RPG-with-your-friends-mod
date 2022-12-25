package io.github.guy7cc.resource;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class DimensionDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DimensionDataManager instance = new DimensionDataManager();

    private List<DimensionData> dataList;

    private DimensionDataManager() {
        super(GSON, "rpgdata/dimension");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        dataList = new ArrayList<>();
        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()){
            ResourceLocation resourcelocation = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                DimensionData data = DimensionData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                dataList.add(data);
            } catch (IllegalArgumentException | JsonParseException | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading rpgw dimension data {}", resourcelocation, exception);
            }
        }
    }

    public DimensionData get(ResourceLocation dimLoc){
        for(DimensionData data : dataList){
            if(data.key().equals(dimLoc)) return data;
        }
        return null;
    }
}
