package io.github.guy7cc.rpg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TraderData implements INBTSerializable<CompoundTag> {
    private List<TradeNode.Buy> buyList = new ArrayList<>();
    private List<TradeNode.Sell> sellList = new ArrayList<>();
    private List<TradeNode.Barter> barterList = new ArrayList<>();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag buy = new CompoundTag();
        int i = 0;
        for(TradeNode.Buy n : buyList){
            buy.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag sell = new CompoundTag();
        i = 0;
        for(TradeNode.Sell n : sellList){
            sell.put(String.format("%02d", i), n.serializeNBT());
            i++;
        }
        CompoundTag barter = new CompoundTag();
        i = 0;
        for(TradeNode.Barter n : barterList){
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

    }
}
