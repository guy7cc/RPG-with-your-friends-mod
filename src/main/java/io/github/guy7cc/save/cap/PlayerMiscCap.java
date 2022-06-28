package io.github.guy7cc.save.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerMiscCap implements INBTSerializable<CompoundTag> {
    public List<ItemStack> keepInventory = new ArrayList<>();

    public PlayerMiscCap() {}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        int i = 0;
        for(ItemStack is : keepInventory){
            nbt.put(String.format("%02d", i++), is.serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt){
        keepInventory = new ArrayList<>();
        List<String> keyList = nbt.getAllKeys().stream().sorted().collect(Collectors.toList());
        for(String key : keyList){
            keepInventory.add(ItemStack.of(nbt.getCompound(key)));
        }
    }
}
