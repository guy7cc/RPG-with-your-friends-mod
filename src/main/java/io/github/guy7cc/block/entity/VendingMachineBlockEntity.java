package io.github.guy7cc.block.entity;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.resource.TraderDataManager;
import io.github.guy7cc.rpg.ITrader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VendingMachineBlockEntity extends BlockEntity implements ITrader {
    private ResourceLocation defaultData;
    private TraderData data;

    public VendingMachineBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.VENDING_MACHINE.get(), pWorldPosition, pBlockState);
        defaultData = new ResourceLocation(RpgwMod.MOD_ID, "default");
        data = TraderDataManager.instance.getDataOrDefault(defaultData);
    }

    public ResourceLocation getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(ResourceLocation location){
        defaultData = TraderDataManager.instance.containsDataKey(location) ? location : new ResourceLocation(RpgwMod.MOD_ID, "default");
        data = TraderDataManager.instance.getDataOrDefault(defaultData);
    }

    @Override
    public TraderData getTraderData(){
        return data;
    }

    @Override
    public void setTraderData(TraderData data){ this.data = data; }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        defaultData = pTag.contains("DefaultData") ? new ResourceLocation(pTag.getString("DefaultData")) : new ResourceLocation(RpgwMod.MOD_ID, "default");
        if(pTag.contains("TraderData")){
            data = new TraderData(pTag.getCompound("TraderData"));
        } else {
            data = TraderDataManager.instance.getDataOrDefault(defaultData);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("DefaultData", defaultData.toString());
        if(data != null) pTag.put("TraderData", data.serializeNBT());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
