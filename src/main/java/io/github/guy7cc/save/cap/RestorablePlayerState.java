package io.github.guy7cc.save.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class RestorablePlayerState implements INBTSerializable<CompoundTag> {


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {

    }
}
