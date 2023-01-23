package io.github.guy7cc.resource;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.network.ClientboundSetRpgStagePacket;
import io.github.guy7cc.network.RpgwMessageManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class RpgStageManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final RpgStageManager instance = new RpgStageManager();

    private Map<ResourceLocation, RpgStage> map = new HashMap<>();

    public RpgStageManager() {
        super(GSON, "rpgdata/stage");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        map.clear();
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

    public boolean containsKey(ResourceLocation location){
        return map.containsKey(location);
    }

    public RpgStage getOrDefault(ResourceLocation location){
        return map.containsKey(location)
                ? map.get(location)
                : RpgStage.DEFAULT;
    }

    public void putToClient(ResourceLocation location, RpgStage stage){
        map.put(location, stage);
    }

    public void syncToClient(ServerPlayer player, ResourceLocation location){
        RpgStage stage = getOrDefault(location);
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSetRpgStagePacket(location, stage));
        for(ResourceLocation scenarioLoc : stage.scenarios()){
            RpgScenarioManager.instance.syncToClient(player, scenarioLoc);
        }
    }
}
