package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;

public class TraderDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final TraderDataManager instance = new TraderDataManager();

    private Map<String, TraderData> dataMap = ImmutableMap.of();
    private Map<String, TraderDataElement.Sell> sellMap = ImmutableMap.of();
    private Map<String, TraderDataElement.Buy> buyMap = ImmutableMap.of();
    private Map<String, TraderDataElement.Barter> barterMap = ImmutableMap.of();

    private TraderDataManager() {
        super(GSON, "traderdata");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ImmutableMap.Builder<String, TraderData> dataBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, TraderDataElement.Sell> sellBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, TraderDataElement.Buy> buyBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, TraderDataElement.Barter> barterBuilder = ImmutableMap.builder();

        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            String path = resourcelocation.getPath();
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.

            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");

                if(resourcelocation.getPath().startsWith("sell/")){
                    TraderDataElement.Sell sell = TraderDataElement.Sell.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    path = resourcelocation.getPath().substring(5);
                    sellBuilder.put(path, sell);
                } else if(resourcelocation.getPath().startsWith("buy/")){
                    TraderDataElement.Buy buy = TraderDataElement.Buy.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    path = resourcelocation.getPath().substring(4);
                    buyBuilder.put(path, buy);
                } else if(resourcelocation.getPath().startsWith("barter/")){
                    TraderDataElement.Barter barter = TraderDataElement.Barter.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    path = resourcelocation.getPath().substring(7);
                    barterBuilder.put(path, barter);
                } else {
                    TraderData data = TraderData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    dataBuilder.put(path, data);
                }
            } catch (IllegalArgumentException | JsonParseException  | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading recipe {}", resourcelocation, exception);
            }
        }

        dataMap = dataBuilder.build();
        sellMap = sellBuilder.build();
        buyMap = buyBuilder.build();
        barterMap = barterBuilder.build();

        LOGGER.info("Loaded {} trader data", dataMap.size());
        LOGGER.info("Loaded {} trader sell elements", sellMap.size());
        LOGGER.info("Loaded {} trader buy elements", buyMap.size());
        LOGGER.info("Loaded {} trader barter elements", barterMap.size());


    }

    public Map<String, TraderDataElement.Sell> getSellMap() {
        return sellMap;
    }

    public Map<String, TraderDataElement.Buy> getBuyMap() {
        return buyMap;
    }

    public Map<String, TraderDataElement.Barter> getBarterMap() {
        return barterMap;
    }

    public Map<String, TraderData> getDataMap() {
        return dataMap;
    }
}
