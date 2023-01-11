package io.github.guy7cc.block.entity;

import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.resource.TraderDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class RpgStageBlockEntity extends BlockEntity {
    private String stage = "default";

    private int swordTick = 0;
    private boolean swordActive = false;
    private AABB renderBoundingBox;

    public RpgStageBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.RPG_STAGE.get(), pWorldPosition, pBlockState);
        renderBoundingBox = new AABB(pWorldPosition).inflate(0, 1d, 0);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return renderBoundingBox;
    }

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, RpgStageBlockEntity pBlockEntity){
        LocalPlayer player = Minecraft.getInstance().player;
        if(pPos.closerToCenterThan(player.position(), 5)){
            pBlockEntity.swordTick++;
            pBlockEntity.swordActive = true;
        }
        else {
            pBlockEntity.swordTick--;
            pBlockEntity.swordActive = false;
        }

        pBlockEntity.swordTick = Math.max(0, Math.min(20, pBlockEntity.swordTick));
    }

    public String getStage(){
        return stage;
    }

    public int getSwordTick(){
        return swordTick;
    }

    public boolean getSwordActive(){
        return swordActive;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        stage = pTag.contains("Stage") ? pTag.getString("Stage") : "default";
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("Stage", stage);
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
