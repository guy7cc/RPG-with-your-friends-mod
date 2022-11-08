package io.github.guy7cc.rpg;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TradeNode implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int WIDTH = 300;
    public static final int HEIGHT = 50;

    private ItemStack itemStack;
    private int id;

    public TradeNode(){
        this.itemStack = ItemStack.EMPTY.copy();
    }

    public TradeNode(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void render(float x, float y) {

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ItemStack", this.itemStack.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ItemStack is = ItemStack.EMPTY.copy();
        is.deserializeNBT(nbt.getCompound("ItemStack"));
        itemStack = is;
    }


    public static class Sell extends TradeNode {
        private long price;

        public Sell(){
            super();
            price = 0;
        }

        public Sell(ItemStack itemStack, long price) {
            super(itemStack);
            this.price = price;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", this.price);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
        }
    }

    public static class Buy extends TradeNode {
        private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        private long price;
        private int count;
        private Date availableFrom;

        public Buy(){
            super();
            price = 0;
            count = 0;
            availableFrom = new Date();
        }

        public Buy(ItemStack itemStack, long price, int count, Date availableFrom) {
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = availableFrom;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", price);
            tag.putInt("Count", count);
            tag.putString("AvailableFrom", format.format(availableFrom));
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
            count = nbt.getInt("Count");
            String date = nbt.getString("AvailableFrom");
            try{
                availableFrom = format.parse(date);
            } catch(ParseException e){
                LOGGER.debug("Invalid string for date: {}", date, e);
                availableFrom = new Date(0);
            }
        }
    }

    public static class Barter extends TradeNode {
        private ItemStack requirement;

        public Barter(){
            super();
            requirement = ItemStack.EMPTY.copy();
        }

        public Barter(ItemStack itemStack, ItemStack requirement){
            super(itemStack);
            this.requirement = requirement;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.put("Requirement", requirement.serializeNBT());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            ItemStack is = ItemStack.EMPTY.copy();
            is.deserializeNBT(nbt.getCompound("Requirement"));
            requirement = is;
        }
    }
}
