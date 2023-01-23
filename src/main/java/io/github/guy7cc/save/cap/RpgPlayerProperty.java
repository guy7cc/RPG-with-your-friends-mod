package io.github.guy7cc.save.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class RpgPlayerProperty implements INBTSerializable<CompoundTag> {
    // Dirty flag (bit mask)
    protected int dirty = 0;

    // Synched fields
    protected float mp = 20;
    protected float maxMp = 20;
    protected long money = 0;
    protected long maxMoney = 10000;

    // Non-synched fields
    public boolean isInventoryKept = false;
    public final List<ItemStack> keptInventory = new ArrayList<>();

    // Server-side constructor
    public RpgPlayerProperty(){ }

    // Client-side constructor
    public RpgPlayerProperty(FriendlyByteBuf buf){
        dirty = buf.readInt();
        for(PropertyType<?> type : PropertyType.allTypes){
            if(isChanged(type)){
                type.read(this, buf);
            }
        }
    }

    public void copy(RpgPlayerProperty property){
        dirty = property.dirty;
        mp = property.mp;
        maxMp = property.maxMp;
        money = property.money;
        maxMoney = property.maxMoney;
        isInventoryKept = property.isInventoryKept;
        keptInventory.clear();
        keptInventory.addAll(property.keptInventory);
    }

    public <T> T getValue(PropertyType<? extends T> type){
        return type.get(this);
    }

    public <T> void setValue(PropertyType<T> type, T value){
        type.set(this, value);
    }

    public <T> void applyFunc(PropertyType<T> type, Function<T, T> func){
        type.set(this, func.apply(getValue(type)));
    }

    public boolean isDirty(){
        return dirty != 0;
    }

    public <T> boolean isChanged(PropertyType<T> type){
        return (dirty & type.bitMask) != 0;
    }

    public void clearDirty(){
        dirty = 0;
    }

    public void setAllDirty(){
        for(PropertyType<?> type : PropertyType.allTypes){
            dirty |= type.bitMask;
        }
    }

    public <T> void writeToBuf(FriendlyByteBuf buf){
        buf.writeInt(dirty);
        for(PropertyType<?> type : PropertyType.allTypes){
            if(isChanged(type)){
                type.write(this, buf);
            }
        }
    }

    protected boolean setMp(float mp) {
        mp = Math.min(mp, maxMp);
        if(this.mp != mp) {
            this.mp = mp;
            return true;
        }
        return false;
    }

    protected boolean setMaxMp(float maxMp) {
        maxMp = Math.max(maxMp, 0);
        if(this.maxMp != maxMp){
            this.maxMp = maxMp;
            return true;
        }
        return false;
    }

    protected boolean setMoney(long money) {
        money = Math.min(money, maxMoney);
        if(this.money != money){
            this.money = money;
            return true;
        }
        return false;
    }

    protected boolean setMaxMoney(long maxMoney) {
        maxMoney = Math.max(maxMoney, 0);
        if(this.maxMoney != maxMoney){
            this.maxMoney = maxMoney;
            return true;
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("Mp", mp);
        tag.putDouble("MaxMp", maxMp);
        tag.putLong("Money", money);
        tag.putLong("MaxMoney", maxMoney);
        tag.putBoolean("IsInventoryKept", isInventoryKept);
        CompoundTag keptInventoryTag = new CompoundTag();
        int i = 0;
        for(ItemStack is : keptInventory){
            keptInventoryTag.put(String.format("%02d", i++), is.serializeNBT());
        }
        tag.put("KeptInventory", keptInventoryTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        mp = tag.getFloat("Mp");
        maxMp = tag.getFloat("MaxMp");
        money = tag.getLong("Money");
        maxMoney = tag.getLong("MaxMoney");
        isInventoryKept = tag.getBoolean("IsInventoryKept");
        CompoundTag keptInventoryTag = tag.getCompound("KeptInventory");
        for(String key : keptInventoryTag.getAllKeys().stream().sorted().collect(Collectors.toList())){
            keptInventory.add(ItemStack.of(keptInventoryTag.getCompound(key)));
        }
    }

}
