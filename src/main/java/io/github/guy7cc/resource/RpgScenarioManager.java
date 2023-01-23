package io.github.guy7cc.resource;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.network.ClientboundSetRpgScenarioPacket;
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

public class RpgScenarioManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final RpgScenarioManager instance = new RpgScenarioManager();

    private Map<ResourceLocation, RpgScenario> map = new HashMap<>();
    private Map<ResourceLocation, RpgScenario> clientMap = new HashMap<>();

    public RpgScenarioManager() {
        super(GSON, "rpgdata/scenario");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        map.clear();
        map.put(new ResourceLocation(RpgwMod.MOD_ID, "default"), RpgScenario.DEFAULT);
        for(Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()){
            ResourceLocation resourcelocation = entry.getKey();
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
                RpgScenario scenario = RpgScenario.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                map.put(resourcelocation, scenario);
            } catch (IllegalArgumentException | JsonParseException | NoSuchElementException exception) {
                LOGGER.error("Parsing error loading rpg scenario {}", resourcelocation, exception);
            }
        }
    }

    public boolean containsKey(ResourceLocation location){
        return map.containsKey(location) || clientMap.containsKey(location);
    }

    public RpgScenario getOrDefault(ResourceLocation location){
        return map.containsKey(location)
                ? map.get(location)
                : clientMap.containsKey(location)
                ? clientMap.get(location)
                : RpgScenario.DEFAULT;
    }

    public void putToClient(ResourceLocation location, RpgScenario scenario){
        clientMap.put(location, scenario);
    }

    public void syncToClient(ServerPlayer player, ResourceLocation location){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player),  new ClientboundSetRpgScenarioPacket(location, getOrDefault(location).reduceSetUp()));
    }
}
