package io.github.guy7cc.resource;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract class TraderDataElement implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int WIDTH = 300;
    public static final int HEIGHT = 50;

    protected ItemStack itemStack;

    public TraderDataElement(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void render(ItemRenderer itemRenderer, float x, float y) {

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


    public static class Sell extends TraderDataElement {
        public static final Codec<Sell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Sell::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(sell -> sell.price),
                Codec.optionalField("next", Codec.pair(Codec.INT, Codec.STRING).listOf()).forGetter(sell -> sell.next)
        ).apply(instance, Sell::new));

        private long price;
        // If you buy A times, a sell element named B will be added.
        private Optional<List<Pair<Integer, String>>> next;

        public Sell(){
            this(ItemStack.EMPTY.copy(), 0, Optional.empty());
        }

        public Sell(ItemStack itemStack, long price, Optional<List<Pair<Integer, String>>> next) {
            super(itemStack);
            this.price = price;
            this.next = next;
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

    public static class Buy extends TraderDataElement {
        public static final Codec<Buy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Buy::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(buy -> buy.price),
                Codec.INT.fieldOf("count").forGetter(buy -> buy.count),
                Codec.STRING.fieldOf("available_from").forGetter(buy -> buy.availableFrom.toString()),
                Codec.LONG.fieldOf("delay").forGetter(buy -> buy.delay),
                Codec.optionalField("next", Codec.STRING.listOf()).forGetter(buy -> buy.next)
        ).apply(instance, Buy::new));
        private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        private long price;
        private int count;
        private Date availableFrom;
        private long delay;
        // If you buy all of these items, a buy element named this will be added.
        private Optional<List<String>> next;

        public Buy(){
            this(ItemStack.EMPTY.copy(), 0, 0, new Date(), 0, Optional.empty());
        }

        public Buy(ItemStack itemStack, long price, int count, Date availableFrom, long delay, Optional<List<String>> next) {
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = availableFrom;
            this.delay = delay;
            this.next = next;
        }

        public Buy(ItemStack itemStack, long price, int count, String stringAvailableFrom, long delay, Optional<List<String>> next){
            super(itemStack);
            this.price = price;
            this.count = count;
            try{
                this.availableFrom = format.parse(stringAvailableFrom);
            } catch(ParseException e){
                LOGGER.debug("Invalid string for date: {}", stringAvailableFrom, e);
                this.availableFrom = new Date(0);
            }
            this.delay = delay;
            this.next = next;
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

    public static class Barter extends TraderDataElement {
        public static final Codec<Barter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Barter::getItemStack),
                ItemStack.CODEC.fieldOf("requirement").forGetter(barter -> barter.requirement),
                Codec.optionalField("next", Codec.pair(Codec.INT, Codec.STRING).listOf()).forGetter(barter -> barter.next)
        ).apply(instance, Barter::new));

        private ItemStack requirement;
        // If you buy A times, a sell element named B will be added.
        private Optional<List<Pair<Integer, String>>> next;

        public Barter(){
            this(ItemStack.EMPTY.copy(), ItemStack.EMPTY.copy(), Optional.empty());
        }

        public Barter(ItemStack itemStack, ItemStack requirement, Optional<List<Pair<Integer, String>>> next){
            super(itemStack);
            this.requirement = requirement;
            this.next = next;
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
