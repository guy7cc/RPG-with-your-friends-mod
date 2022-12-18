package io.github.guy7cc.block.entity;

import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.resource.TraderDataManager;
import io.github.guy7cc.rpg.ITrader;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VendingMachineBlockEntity extends BlockEntity implements ITrader {
    private String defaultData;
    private TraderData data;

    public VendingMachineBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.VENDING_MACHINE.get(), pWorldPosition, pBlockState);
        defaultData = "test";
        data = TraderDataManager.instance.getData(defaultData);
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
        defaultData = pTag.contains("DefaultData") ? pTag.getString("DefaultData") : "test";
        if(pTag.contains("TraderData")){
            data = new TraderData(pTag.getCompound("TraderData"));
        } else {
            data = TraderDataManager.instance.getData(defaultData);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("DefaultData", defaultData);
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
