package io.github.guy7cc.rpg;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class TraderData implements INBTSerializable<CompoundTag> {
    private List<TradeNode.Sell> sellList = new ArrayList<>();
    private List<TradeNode.Buy> buyList = new ArrayList<>();
    private List<TradeNode.Barter> barterList = new ArrayList<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag sell = new CompoundTag();
        int i = 0;
        for(TradeNode.Sell n : sellList){
            sell.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag buy = new CompoundTag();
        i = 0;
        for(TradeNode.Buy n : buyList){
            buy.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag barter = new CompoundTag();
        i = 0;
        for(TradeNode.Barter n : barterList){
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
            TradeNode.Sell node = new TradeNode.Sell();
            node.deserializeNBT(sell.getCompound(key));
            sellList.add(node);
        }
        for(String key : buy.getAllKeys()){
            TradeNode.Buy node = new TradeNode.Buy();
            node.deserializeNBT(buy.getCompound(key));
            buyList.add(node);
        }
        for(String key : barter.getAllKeys()){
            TradeNode.Barter node = new TradeNode.Barter();
            node.deserializeNBT(barter.getCompound(key));
            barterList.add(node);
        }
    }
}
