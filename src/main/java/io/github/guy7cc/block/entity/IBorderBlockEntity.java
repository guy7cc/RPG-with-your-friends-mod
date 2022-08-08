package io.github.guy7cc.block.entity;

import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface IBorderBlockEntity {
    Border getBorder();

    void setBorder(Border border);

    default void loadBorder(CompoundTag pTag) {
        Border border = new Border();
        if(pTag.contains("Border")) border.deserializeNBT(pTag.getCompound("Border"));
        setBorder(border);
    }

    default void saveBorder(CompoundTag pTag) {
        Border border = getBorder();
        if(border != null) pTag.put("Border", border.serializeNBT());
    }

    default void tickBorder(int tickCount, Level level, BlockPos pos, BlockState state, BlockEntity blockEntity){
        if(!level.isClientSide){
            if(tickCount % 10 == 0){
                ServerLevel serverLevel = (ServerLevel) level;
                Border border = getBorder();
                for(ServerPlayer player : serverLevel.players()){
                    if(border.inside(player.getPosition(1))){
                        BorderManager.applyIfAbsent(player, border);
                    }
                }
            }
        }
    }


}
