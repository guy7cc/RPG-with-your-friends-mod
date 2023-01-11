package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableMap;
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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TraderDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final TraderDataManager instance = new TraderDataManager();

    private Map<ResourceLocation, TraderData> dataMap = new HashMap<>();
    private Map<ResourceLocation, TraderDataElement.Buy> buyMap = new HashMap<>();
    private Map<ResourceLocation, TraderDataElement.Sell> sellMap = new HashMap<>();
    private Map<ResourceLocation, TraderDataElement.Barter> barterMap = new HashMap<>();

    private TraderDataManager() {
        super(GSON, "traderdata");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        buyMap.clear();
        sellMap.clear();
        barterMap.clear();
        dataMap.clear();

        ResourceLocation defaultLoc = new ResourceLocation(RpgwMod.MOD_ID, "default");
        buyMap.put(defaultLoc, TraderDataElement.Buy.DEFAULT);
        sellMap.put(defaultLoc, TraderDataElement.Sell.DEFAULT);
        barterMap.put(defaultLoc, TraderDataElement.Barter.DEFAULT);
        dataMap.put(defaultLoc, TraderData.DEFAULT);

        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            String path = resourcelocation.getPath();

            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");

                if(resourcelocation.getPath().startsWith("buy/")){
                    TraderDataElement.Buy buy = TraderDataElement.Buy.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    ResourceLocation loc = new ResourceLocation(resourcelocation.getNamespace(), resourcelocation.getPath().substring(4));
                    buyMap.put(loc, buy);
                } else if(resourcelocation.getPath().startsWith("sell/")){
                    TraderDataElement.Sell sell = TraderDataElement.Sell.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    ResourceLocation loc = new ResourceLocation(resourcelocation.getNamespace(), resourcelocation.getPath().substring(5));
                    sellMap.put(loc, sell);
                } else if(resourcelocation.getPath().startsWith("barter/")){
                    TraderDataElement.Barter barter = TraderDataElement.Barter.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    ResourceLocation loc = new ResourceLocation(resourcelocation.getNamespace(), resourcelocation.getPath().substring(7));
                    barterMap.put(loc, barter);
                } else {
                    TraderData data = TraderData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                    dataMap.put(resourcelocation, data);
                }
            } catch (IllegalArgumentException | JsonParseException  | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading trader data {}", resourcelocation, exception);
            }
        }

        LOGGER.info("Loaded {} trader data", dataMap.size());
        LOGGER.info("Loaded {} trader buy elements", buyMap.size());
        LOGGER.info("Loaded {} trader sell elements", sellMap.size());
        LOGGER.info("Loaded {} trader barter elements", barterMap.size());
    }

    public TraderDataElement.Buy getBuy(ResourceLocation key) {
        TraderDataElement.Buy buy = buyMap.get(key);
        return buy != null ? (TraderDataElement.Buy) buy.copy() : null;
    }

    public TraderDataElement.Sell getSell(ResourceLocation key) {
        TraderDataElement.Sell sell = sellMap.get(key);
        return sell != null ? (TraderDataElement.Sell) sell.copy() : null;
    }

    public TraderDataElement.Barter getBarter(ResourceLocation key) {
        TraderDataElement.Barter barter = barterMap.get(key);
        return barter != null ? (TraderDataElement.Barter) barter.copy() : null;
    }

    public TraderData getData(ResourceLocation key) {
        TraderData data = dataMap.get(key);
        return data != null ? (TraderData) data.copy() : null;
    }
}
