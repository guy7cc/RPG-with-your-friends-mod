package io.github.guy7cc.save;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RpgScenarioSavedData implements INBTSerializable<CompoundTag> {
    private Set<UUID> passedPlayer = new HashSet<>();

    public RpgScenarioSavedData(){ }

    public boolean passed(ServerPlayer player){
        return passedPlayer.contains(player.getUUID());
    }

    public void add(ServerPlayer player){
        passedPlayer.add(player.getUUID());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag passedTag = new CompoundTag();
        int i = 0;
        for(UUID uuid : passedPlayer){
            passedTag.putUUID(Integer.toString(i), uuid);
            i++;
        }
        tag.put("Passed", passedTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        CompoundTag passedTag = tag.getCompound("Passed");
        for(String key : passedTag.getAllKeys()){
            passedPlayer.add(passedTag.getUUID(key));
        }
    }
}
