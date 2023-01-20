package io.github.guy7cc.save.cap;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class RpgPlayerProperty implements INBTSerializable<CompoundTag> {
    // Dirty flag (bit mask)
    private int dirty = 0;

    // Synched fields
    private double mp = 20;
    private double maxMp = 20;
    private long money = 0;
    private long maxMoney = 10000;

    // Non-synched fields
    public boolean isInventoryKept = false;
    private List<ItemStack> keptInventory = new ArrayList<>();

    // Server-side constructor
    public RpgPlayerProperty(){ }

    // Client-side constructor
    public RpgPlayerProperty(FriendlyByteBuf buf){
        dirty = buf.readInt();
        for(DataType<?> type : DataType.allTypes){
            if(isChanged(type)){
                type.read(this, buf);
            }
        }
    }

    public <T> T getValue(DataType<T> type){
        return type.get(this);
    }

    public <T> void setValue(DataType<T> type, T value){
        type.set(this, value);
    }

    public boolean isDirty(){
        return dirty != 0;
    }

    public <T> boolean isChanged(DataType<T> type){
        return (dirty & type.bitMask) != 0;
    }

    public <T> void writeToBuf(FriendlyByteBuf buf){
        buf.writeInt(dirty);
        for(DataType<?> type : DataType.allTypes){
            if(isChanged(type)){
                type.write(this, buf);
            }
        }
    }

    private boolean setMp(double mp) {
        mp = Math.min(mp, maxMp);
        if(this.mp != mp) {
            this.mp = mp;
            return true;
        }
        return false;
    }

    private boolean setMaxMp(double maxMp) {
        maxMp = Math.max(maxMp, 0);
        if(this.maxMp != maxMp){
            this.maxMp = maxMp;
            return true;
        }
        return false;
    }

    private boolean setMoney(long money) {
        money = Math.min(money, maxMoney);
        if(this.money != money){
            this.money = money;
            return true;
        }
        return false;
    }

    private boolean setMaxMoney(long maxMoney) {
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
            keptInventoryTag.put(String.format("%03d", i++), is.serializeNBT());
        }
        tag.put("KeptInventory", keptInventoryTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        mp = tag.getDouble("Mp");
        maxMp = tag.getDouble("MaxMp");
        money = tag.getLong("Money");
        maxMoney = tag.getLong("MaxMoney");
        isInventoryKept = tag.getBoolean("IsInventoryKept");
        CompoundTag keptInventoryTag = tag.getCompound("KeptInventory");
        for(String key : keptInventoryTag.getAllKeys()){
            ItemStack is = ItemStack.EMPTY.copy();
            is.deserializeNBT(keptInventoryTag.getCompound(key));
            keptInventory.add(is);
        }
    }

    public static final class DataType<T> {
        public static final DataType MP = new DataType<>(1, p -> p.mp, (p, mp) -> p.setMp(mp), (p, buf) -> buf.writeDouble(p.mp), (p, buf) -> p.mp = buf.readDouble());
        public static final DataType MAX_MP = new DataType<>(2, p -> p.maxMp, (p, maxMp) -> p.setMaxMp(maxMp), (p, buf) -> buf.writeDouble(p.maxMp), (p, buf) -> p.maxMp = buf.readDouble());
        public static final DataType MONEY = new DataType<>(4, p -> p.money, (p, money) -> p.setMoney(money), (p, buf) -> buf.writeLong(p.money), (p, buf) -> p.money = buf.readLong());
        public static final DataType MAX_MONEY = new DataType<>(8, p -> p.maxMoney, (p, maxMoney) -> p.setMaxMoney(maxMoney), (p, buf) -> buf.writeLong(p.maxMoney), (p, buf) -> p.maxMoney = buf.readLong());
        public static final ImmutableList<DataType<?>> allTypes = ImmutableList.of(MP, MAX_MP, MONEY, MAX_MONEY);

        private final int bitMask;
        private final Function<RpgPlayerProperty, T> getter;
        private final BiFunction<RpgPlayerProperty, T, Boolean> setter;
        private final BiConsumer<RpgPlayerProperty, FriendlyByteBuf> writer;
        private final BiConsumer<RpgPlayerProperty, FriendlyByteBuf> reader;

        private DataType(int bitMask, Function<RpgPlayerProperty, T> getter, BiFunction<RpgPlayerProperty, T, Boolean> setter, BiConsumer<RpgPlayerProperty, FriendlyByteBuf> writer, BiConsumer<RpgPlayerProperty, FriendlyByteBuf> reader) {
            this.bitMask = bitMask;
            this.getter = getter;
            this.setter = setter;
            this.writer = writer;
            this.reader = reader;
        }

        private T get(RpgPlayerProperty p) {
            return getter.apply(p);
        }

        private void set(RpgPlayerProperty p, T value) {
            if(setter.apply(p, value)){
                p.dirty |= bitMask;
            }
        }

        private void write(RpgPlayerProperty p, FriendlyByteBuf buf){
            writer.accept(p, buf);
        }

        private void read(RpgPlayerProperty p, FriendlyByteBuf buf){
            reader.accept(p, buf);
        }
    }
}
