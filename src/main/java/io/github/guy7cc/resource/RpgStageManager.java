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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class RpgStageManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final RpgStageManager instance = new RpgStageManager();

    private Map<ResourceLocation, RpgStage> map;

    public RpgStageManager() {
        super(GSON, "rpgdata/stage");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        map = new HashMap<>();
        map.put(new ResourceLocation(RpgwMod.MOD_ID, "default"), RpgStage.DEFAULT);
        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()){
            ResourceLocation resourcelocation = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                RpgStage stage = RpgStage.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                map.put(resourcelocation, stage);
            } catch (IllegalArgumentException | JsonParseException | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading rpg stage {}", resourcelocation, exception);
            }
        }
    }

    public RpgStage get(ResourceLocation location){
        return map.get(location);
    }
}
