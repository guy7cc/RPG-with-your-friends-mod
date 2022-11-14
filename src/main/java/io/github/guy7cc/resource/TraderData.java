package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class TraderData implements INBTSerializable<CompoundTag> {
    public static final Codec<TraderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TraderDataElement.Sell.CODEC.listOf().fieldOf("sell_list").forGetter(data -> data.sellList),
            TraderDataElement.Buy.CODEC.listOf().fieldOf("buy_list").forGetter(data -> data.buyList),
            TraderDataElement.Barter.CODEC.listOf().fieldOf("barter_list").forGetter(data -> data.barterList)
    ).apply(instance, TraderData::new));

    private List<TraderDataElement.Sell> sellList;
    private List<TraderDataElement.Buy> buyList;
    private List<TraderDataElement.Barter> barterList;

    public TraderData(){
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public TraderData(List<TraderDataElement.Sell> sellList, List<TraderDataElement.Buy> buyList, List<TraderDataElement.Barter> barterList){
        this.sellList = sellList;
        this.buyList = buyList;
        this.barterList = barterList;
    }

    public ImmutableList<? extends TraderDataElement> getList(int index){
        if(index == 0) return ImmutableList.copyOf(sellList);
        else if(index == 1) return ImmutableList.copyOf(buyList);
        else if(index == 2) return ImmutableList.copyOf(barterList);
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag sell = new CompoundTag();
        int i = 0;
        for(TraderDataElement.Sell n : sellList){
            sell.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag buy = new CompoundTag();
        i = 0;
        for(TraderDataElement.Buy n : buyList){
            buy.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag barter = new CompoundTag();
        i = 0;
        for(TraderDataElement.Barter n : barterList){
            barter.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag tag = new CompoundTag();
        tag.put("Sell", sell);
        tag.put("Buy", buy);
        tag.put("Barter", barter);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        sellList.clear();
        buyList.clear();
        barterList.clear();
        CompoundTag sell = nbt.getCompound("Sell");
        CompoundTag buy = nbt.getCompound("Buy");
        CompoundTag barter = nbt.getCompound("Barter");
        for(String key : sell.getAllKeys()){
            TraderDataElement.Sell node = new TraderDataElement.Sell();
            node.deserializeNBT(sell.getCompound(key));
            sellList.add(node);
        }
        for(String key : buy.getAllKeys()){
            TraderDataElement.Buy node = new TraderDataElement.Buy();
            node.deserializeNBT(buy.getCompound(key));
            buyList.add(node);
        }
        for(String key : barter.getAllKeys()){
            TraderDataElement.Barter node = new TraderDataElement.Barter();
            node.deserializeNBT(barter.getCompound(key));
            barterList.add(node);
        }
    }
}
