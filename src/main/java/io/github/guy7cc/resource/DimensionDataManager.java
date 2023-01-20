package io.github.guy7cc.resource;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.ScreenOpenEvent;
import org.slf4j.Logger;

import java.util.*;

public class DimensionDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DimensionDataManager instance = new DimensionDataManager();

    private Map<ResourceLocation, DimensionData> map = new HashMap<>();

    private DimensionDataManager() {
        super(GSON, "rpgdata/dimension");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        map.clear();
        map.put(new ResourceLocation(RpgwMod.MOD_ID, "default"), DimensionData.DEFAULT);
        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()){
            ResourceLocation resourcelocation = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                DimensionData data = DimensionData.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                map.put(data.key(), data);
            } catch (IllegalArgumentException | JsonParseException | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading rpgw dimension data {}", resourcelocation, exception);
            }
        }
    }

    public boolean containsKey(ResourceLocation dimLoc){
        return map.containsKey(dimLoc);
    }

    public DimensionData getOrDefault(ResourceLocation dimLoc){
        return map.containsKey(dimLoc) ? map.get(dimLoc) : DimensionData.DEFAULT;
    }

    public static void onScreenOpen(ScreenOpenEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen instanceof ReceivingLevelScreen && instance.containsKey(minecraft.level.dimension().location())){
            instance.getOrDefault(minecraft.level.dimension().location()).show();
        }
    }
}
