package io.github.guy7cc.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public abstract class TraderDataElement implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected ItemStack itemStack;

    public TraderDataElement(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public TraderDataElement(CompoundTag tag){
        deserializeNBT(tag);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public abstract TraderDataElement copy();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ItemStack", this.itemStack.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        itemStack = ItemStack.of(nbt.getCompound("ItemStack"));
    }


    public static class Buy extends TraderDataElement {
        public static final Codec<Buy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Buy::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(sell -> sell.price),
                Codec.optionalField("next", Codec.pair(Codec.INT.fieldOf("count").codec(), ResourceLocation.CODEC.fieldOf("name").codec()).listOf()).forGetter(sell -> sell.next)
        ).apply(instance, Buy::new));

        public static final Buy DEFAULT = new Buy(new ItemStack(Items.BARRIER), 1000, Optional.empty());

        private long price;
        // If you buy A times, a sell element named B will be added.
        private Optional<List<Pair<Integer, ResourceLocation>>> next;

        public Buy(){
            this(ItemStack.EMPTY.copy(), 0, Optional.empty());
        }

        public Buy(ItemStack itemStack, long price, Optional<List<Pair<Integer, ResourceLocation>>> next) {
            super(itemStack);
            this.price = price;
            this.next = next;
        }

        public Buy(CompoundTag tag){
            super(tag);
        }

        public long getPrice() {
            return price;
        }

        public TraderDataElement copy(){
            List<Pair<Integer, ResourceLocation>> list = next.orElse(null);
            if(list != null){
                List<Pair<Integer, ResourceLocation>> newList = new ArrayList<>();
                for(Pair<Integer, ResourceLocation> pair : list){
                    newList.add(new Pair<>(pair.getFirst(), pair.getSecond()));
                }
                return new Buy(itemStack.copy(), price, Optional.of(newList));
            } else return new Buy(itemStack.copy(), price, Optional.empty());
        }

        public List<Buy> confirmTrade(int count){
            List<Pair<Integer, ResourceLocation>> list = next.orElse(null);
            List<Buy> add = new ArrayList<>();
            if(list != null){
                List<Pair<Integer, ResourceLocation>> newList = new ArrayList<>();
                for(Pair<Integer, ResourceLocation> pair : list){
                    if(pair.getFirst() <= count){
                        Buy buy = TraderDataManager.instance.getBuyOrDefault(pair.getSecond());
                        if(buy != null) add.add(buy);
                    } else{
                        newList.add(new Pair<>(pair.getFirst() - count, pair.getSecond()));
                    }
                }
                next = Optional.of(newList);
            }
            return add;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", this.price);
            List<Pair<Integer, ResourceLocation>> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putInt("Count", list.get(i).getFirst());
                    p.putString("Name", list.get(i).getSecond().toString());
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
            if(nbt.contains("Next")){
                List<Pair<Integer, ResourceLocation>> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    Pair<Integer, ResourceLocation> pair = new Pair<>(p.getInt("Count"), new ResourceLocation(p.getString("Name")));
                    list.add(pair);
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public static class Sell extends TraderDataElement {
        public static final Codec<Sell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Sell::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(buy -> buy.price),
                Codec.INT.fieldOf("count").forGetter(buy -> buy.count),
                Codec.LONG.fieldOf("available_from").forGetter(buy -> buy.availableFrom.toEpochMilli()),
                Codec.LONG.fieldOf("delay").forGetter(buy -> buy.delay),
                Codec.optionalField("next", ResourceLocation.CODEC.listOf()).forGetter(buy -> buy.next)
        ).apply(instance, Sell::new));
        private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        public static final Sell DEFAULT = new Sell(new ItemStack(Items.BARRIER), 1000, 1, Instant.ofEpochMilli(0), 1000, Optional.empty());

        private long price;
        private int count;
        private Instant availableFrom;
        private long delay;
        // If you buy all of these items, this element will be deleted, and a buy element named this will be added.
        private Optional<List<ResourceLocation>> next;

        public Sell(){
            this(ItemStack.EMPTY.copy(), 0, 0, Instant.ofEpochMilli(0), 0, Optional.empty());
        }

        public Sell(ItemStack itemStack, long price, int count, Instant availableFrom, long delay, Optional<List<ResourceLocation>> next) {
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = availableFrom;
            this.delay = delay;
            this.next = next;
        }

        public Sell(CompoundTag tag){
            super(tag);
        }

        public Sell(ItemStack itemStack, long price, int count, long longAvailableFrom, long delay, Optional<List<ResourceLocation>> next){
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = Instant.ofEpochMilli(longAvailableFrom);
            this.delay = delay;
            this.next = next;
        }

        public long getPrice() { return price; }

        public int getCount() { return count; }

        public long getAvailableFrom(){ return availableFrom.toEpochMilli(); }

        public void setAvailableFrom(long milli){
            availableFrom = Instant.ofEpochMilli(milli);
        }

        public long getDelay(){
            return delay;
        }

        public TraderDataElement copy(){
            List<ResourceLocation> list = next.orElse(null);
            if(list != null){
                List<ResourceLocation> newList = new ArrayList<>(list);
                return new Sell(itemStack.copy(), price, count, availableFrom, delay, Optional.of(newList));
            } else return new Sell(itemStack.copy(), price, count, Instant.ofEpochMilli(availableFrom.toEpochMilli()), delay, Optional.empty());
        }

        public List<Sell> confirmTrade(int count){
            this.count -= count;
            List<Sell> add = new ArrayList<>();
            if(this.count <= 0){
                List<ResourceLocation> list = next.orElse(null);
                if(list != null){
                    for(ResourceLocation key : list){
                        Sell sell = TraderDataManager.instance.getSellOrDefault(key);
                        if(sell != null) add.add(sell);
                    }
                }
            }
            return add;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", price);
            tag.putInt("Count", count);
            tag.putLong("AvailableFrom", availableFrom.toEpochMilli());
            tag.putLong("Delay", delay);
            List<ResourceLocation> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putString("Name", list.get(i).toString());
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
            count = nbt.getInt("Count");
            availableFrom = Instant.ofEpochMilli(nbt.getLong("AvailableFrom"));
            delay = nbt.getLong("Delay");
            if(nbt.contains("Next")){
                List<ResourceLocation> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    list.add(new ResourceLocation(p.getString("Name")));
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public static class Barter extends TraderDataElement {
        public static final Codec<Barter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Barter::getItemStack),
                ItemStack.CODEC.fieldOf("requirement").forGetter(barter -> barter.requirement),
                Codec.optionalField("next", Codec.pair(Codec.INT.fieldOf("count").codec(), ResourceLocation.CODEC.fieldOf("name").codec()).listOf()).forGetter(barter -> barter.next)
        ).apply(instance, Barter::new));

        public static final Barter DEFAULT = new Barter(new ItemStack(Items.BARRIER), new ItemStack(Items.BARRIER), Optional.empty());

        private ItemStack requirement;
        // If you buy A times, a barter element named B will be added.
        private Optional<List<Pair<Integer, ResourceLocation>>> next;

        public Barter(){
            this(ItemStack.EMPTY.copy(), ItemStack.EMPTY.copy(), Optional.empty());
        }

        public Barter(ItemStack itemStack, ItemStack requirement, Optional<List<Pair<Integer, ResourceLocation>>> next){
            super(itemStack);
            this.requirement = requirement;
            this.next = next;
        }

        public Barter(CompoundTag tag){
            super(tag);
        }

        public ItemStack getRequirement() {
            return requirement;
        }

        public TraderDataElement copy(){
            return new Barter(itemStack, requirement, next);
        }

        public List<Barter> confirmTrade(int count){
            List<Pair<Integer, ResourceLocation>> list = next.orElse(null);
            List<Barter> add = new ArrayList<>();
            if(list != null){
                List<Pair<Integer, ResourceLocation>> newList = new ArrayList<>();
                for(Pair<Integer, ResourceLocation> pair : list){
                    if(pair.getFirst() <= count){
                        Barter barter = TraderDataManager.instance.getBarterOrDefault(pair.getSecond());
                        if(barter != null) add.add(barter);
                    } else{
                        newList.add(new Pair<>(pair.getFirst() - count, pair.getSecond()));
                    }
                }
                next = Optional.of(newList);
            }
            return add;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.put("Requirement", requirement.serializeNBT());
            List<Pair<Integer, ResourceLocation>> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putInt("Count", list.get(i).getFirst());
                    p.putString("Name", list.get(i).getSecond().toString());
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            requirement = ItemStack.of(nbt.getCompound("Requirement"));
            if(nbt.contains("Next")){
                List<Pair<Integer, ResourceLocation>> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    Pair<Integer, ResourceLocation> pair = new Pair<>(p.getInt("Count"), new ResourceLocation(p.getString("Name")));
                    list.add(pair);
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public enum Type{
        Buy(0),
        Sell(1),
        Barter(2);

        public final int id;
        Type(int id){
            this.id = id;
        }

        public static Type fromId(int id){
            if(id == 0) return Buy;
            else if(id == 1) return Sell;
            else if(id == 2) return Barter;
            else return null;
        }
    }
}
