package io.github.guy7cc.save;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class RpgSavedData extends SavedData {
    private final Map<ResourceLocation, RpgScenarioSavedData> scenarioMap = new HashMap<>();

    public RpgSavedData(){

    }

    public RpgScenarioSavedData getOrCreateRpgScenarioSavedData(ResourceLocation scenarioLoc){
        if(scenarioMap.containsKey(scenarioLoc)) return scenarioMap.get(scenarioLoc);
        else {
            RpgScenarioSavedData newData = new RpgScenarioSavedData();
            scenarioMap.put(scenarioLoc, newData);
            return newData;
        }
    }

    public static RpgSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(RpgSavedData::load, RpgSavedData::new, "rpgsaveddata");
    }

    public static RpgSavedData load(CompoundTag tag){
        RpgSavedData data = new RpgSavedData();
        CompoundTag scenarioTag = tag.getCompound("RpgScenario");
        for(String key : scenarioTag.getAllKeys()){
            ResourceLocation scenarioLoc = new ResourceLocation(key);
            RpgScenarioSavedData scenarioData = new RpgScenarioSavedData();
            scenarioData.deserializeNBT(scenarioTag.getCompound(key));
            data.scenarioMap.put(scenarioLoc, scenarioData);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag scenarioTag = new CompoundTag();
        for(Map.Entry<ResourceLocation, RpgScenarioSavedData> entry : scenarioMap.entrySet()){
            scenarioTag.put(entry.getKey().toString(), entry.getValue().serializeNBT());
        }
        tag.put("RpgScenario", scenarioTag);
        return tag;
    }
}
