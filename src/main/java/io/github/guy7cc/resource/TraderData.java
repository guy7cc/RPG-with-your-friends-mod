package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.save.cap.PropertyType;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.save.cap.RpgPlayerPropertyManager;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TraderData implements INBTSerializable<CompoundTag> {
    public static final Codec<TraderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TraderDataElement.Buy.CODEC.listOf().fieldOf("buy_list").forGetter(data -> data.buyList),
            TraderDataElement.Sell.CODEC.listOf().fieldOf("sell_list").forGetter(data -> data.sellList),
            TraderDataElement.Barter.CODEC.listOf().fieldOf("barter_list").forGetter(data -> data.barterList)
    ).apply(instance, TraderData::new));

    public static final TraderData DEFAULT = new TraderData(List.of(TraderDataElement.Buy.DEFAULT), List.of(TraderDataElement.Sell.DEFAULT), List.of(TraderDataElement.Barter.DEFAULT));

    private List<TraderDataElement.Buy> buyList;
    private List<TraderDataElement.Sell> sellList;
    private List<TraderDataElement.Barter> barterList;

    public TraderData(){
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public TraderData(List<TraderDataElement.Buy> buyList, List<TraderDataElement.Sell> sellList, List<TraderDataElement.Barter> barterList){
        this.buyList = new ArrayList<>(buyList);
        this.sellList = new ArrayList<>(sellList);
        this.barterList = new ArrayList<>(barterList);
    }

    public TraderData(CompoundTag tag){
        deserializeNBT(tag);
    }

    public ImmutableList<? extends TraderDataElement> getList(int index){
        if(index == 0) return ImmutableList.copyOf(buyList);
        else if(index == 1) return ImmutableList.copyOf(sellList);
        else if(index == 2) return ImmutableList.copyOf(barterList);
        return null;
    }

    public void confirmTrade(ServerPlayer player, int type, int index, int count){
        List<? extends TraderDataElement> list = getList(type);
        if(index < 0 || list.size() <= index) return;
        switch(type){
            case 0:
                TraderDataElement.Buy buy = buyList.get(index);
                RpgPlayerProperty p1 = RpgPlayerPropertyManager.get(player);
                if(p1 == null) return;
                if(p1.getValue(PropertyType.MONEY) / buy.getPrice() < count) return;
                p1.applyFunc(PropertyType.MONEY, m -> m - buy.getPrice() * count);
                ItemStack is1 = buy.getItemStack().copy();
                is1.setCount(is1.getCount() * count);
                addItemStack(player, is1);
                buyList.addAll(buy.confirmTrade(count));
                break;
            case 1:
                TraderDataElement.Sell sell = sellList.get(index);
                RpgPlayerProperty p2 = RpgPlayerPropertyManager.get(player);
                if(p2 == null) return;
                int amount1 = getAmountInInventory(player, sell.getItemStack());
                if(amount1 / sell.getItemStack().getCount() < count) return;
                p2.applyFunc(PropertyType.MONEY, m -> m + sell.getPrice() * count);
                ItemStack is2 = sell.getItemStack().copy();
                is2.setCount(is2.getCount() * count);
                removeItemStack(player, is2);
                sellList.addAll(sell.confirmTrade(count));
                if(sell.getCount() <= 0) {
                    sellList.remove(sell);

                    //delay the sell element which has the same item stack and price
                    sellList.stream()
                            .filter(s -> s.getItemStack().equals(sell.getItemStack(), true) && s.getPrice() == sell.getPrice())
                            .forEach(s -> s.setAvailableFrom(Instant.now().toEpochMilli() + sell.getDelay()));
                }
                break;
            case 2:
                TraderDataElement.Barter barter = (TraderDataElement.Barter) list.get(index);
                int amount2 = getAmountInInventory(player, barter.getRequirement());
                if(amount2 / barter.getRequirement().getCount() < count) return;
                ItemStack is3 = barter.getItemStack().copy();
                ItemStack is4 = barter.getRequirement().copy();
                is3.setCount(is3.getCount() * count);
                is4.setCount(is4.getCount() * count);
                removeItemStack(player, is4);
                addItemStack(player, is3);
                barter.confirmTrade(count);
                break;
        }
    }

    public TraderData copy(){
        List<TraderDataElement.Buy> newBuyList = buyList.stream().map(b -> (TraderDataElement.Buy)b.copy()).toList();
        List<TraderDataElement.Sell> newSellList = sellList.stream().map(s ->(TraderDataElement.Sell)s.copy()).toList();
        List<TraderDataElement.Barter> newBarterList = barterList.stream().map(b -> (TraderDataElement.Barter)b.copy()).toList();
        return new TraderData(newBuyList, newSellList, newBarterList);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag buy = new CompoundTag();
        int i = 0;
        for(TraderDataElement.Buy n : buyList){
            buy.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag sell = new CompoundTag();
        i = 0;
        for(TraderDataElement.Sell n : sellList){
            sell.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag barter = new CompoundTag();
        i = 0;
        for(TraderDataElement.Barter n : barterList){
            barter.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Buy", buy);
        tag.put("Sell", sell);
        tag.put("Barter", barter);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        buyList = new ArrayList<>();
        sellList = new ArrayList<>();
        barterList = new ArrayList<>();
        CompoundTag buy = nbt.getCompound("Buy");
        CompoundTag sell = nbt.getCompound("Sell");
        CompoundTag barter = nbt.getCompound("Barter");
        for(String key : buy.getAllKeys()){
            buyList.add(new TraderDataElement.Buy(buy.getCompound(key)));
        }
        for(String key : sell.getAllKeys()){
            sellList.add(new TraderDataElement.Sell(sell.getCompound(key)));
        }
        for(String key : barter.getAllKeys()){
            barterList.add(new TraderDataElement.Barter(barter.getCompound(key)));
        }
    }

    private int getAmountInInventory(ServerPlayer player, ItemStack itemstack){
        int a = 0;
        Inventory inv = player.getInventory();
        for(int i = 0; i < 41; i++){
            ItemStack is = inv.getItem(i);
            if(is.sameItem(itemstack)){
                a += is.getCount();
            }
        }
        return a;
    }

    private void addItemStack(ServerPlayer player, ItemStack itemStack){
        Inventory inv = player.getInventory();
        /*
        for(int i = 0; i < inv.items.size() && itemStack.getCount() > 0; i++){
            ItemStack is = inv.getItem(i);
            if(is.sameItem(itemStack)){
                if(is.getMaxStackSize() - is.getCount() >= itemStack.getCount()) {
                    is.setCount(is.getCount() + itemStack.getCount());
                    itemStack.setCount(0);
                }
                else{
                    itemStack.shrink(is.getMaxStackSize() - is.getCount());
                    is.setCount(is.getMaxStackSize());
                }
                inv.setItem(i, is);
            }
        }
        for(int i = 0; i < inv.items.size() && itemStack.getCount() > 0; i++){
            ItemStack is = inv.getItem(i);
            if(is.isEmpty()){
                int count = Math.min(itemStack.getCount(), itemStack.getMaxStackSize());
                itemStack.shrink(count);
                ItemStack is2 = itemStack.copy();
                is2.setCount(count);
                inv.setItem(i, is2);
            }
        }
        */


        int free = 0;
        for(int i = 0; i < inv.items.size() && itemStack.getCount() > 0; i++){
            ItemStack is = inv.getItem(i);
            if(is.sameItem(itemStack)){
                free += itemStack.getMaxStackSize() - is.getCount();
            } else if(is.isEmpty()){
                free += itemStack.getMaxStackSize();
            }
        }
        if(free >= itemStack.getCount()){
            inv.add(itemStack);
        } else {
            ItemStack is1 = itemStack.copy();
            ItemStack is2 = itemStack.copy();
            is1.setCount(free);
            is2.setCount(itemStack.getCount() - free);
            inv.add(is1);
            ServerLevel level = player.getLevel();
            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), is2, 0, 0.1, 0));
        }

    }

    private int removeItemStack(ServerPlayer player, ItemStack itemStack){
        NonNullList<ItemStack> items = player.getInventory().items;
        int count = itemStack.getCount();
        for(int i = 0; i < items.size() && count > 0; i++){
            ItemStack is = items.get(i);
            if(is.sameItem(itemStack)) {
                if (count < is.getCount()) {
                    is.shrink(count);
                    count = 0;
                    break;
                } else {
                    count -= is.getCount();
                    items.set(i, ItemStack.EMPTY.copy());
                }
            }
        }
        return count;
    }
}
