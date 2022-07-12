package io.github.guy7cc.block.entity;

import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBorderBlockEntity extends BlockEntity {
    private static int borderIdCount = 0;

    public Border border;

    private int tickCount = 0;

    public AbstractBorderBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        this(pType, pWorldPosition, pBlockState, pWorldPosition.getX() - 3, pWorldPosition.getX() + 4, pWorldPosition.getZ() - 3, pWorldPosition.getZ() + 4);
    }

    public AbstractBorderBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, double minX, double maxX, double minZ, double maxZ){
        super(pType, pWorldPosition, pBlockState);
        this.border = new Border(borderIdCount++, minX, maxX, minZ, maxZ);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractBorderBlockEntity blockEntity){
        if(!level.isClientSide){
            blockEntity.tickCount++;
            if(blockEntity.tickCount % 10 == 0){
                ServerLevel serverLevel = (ServerLevel) level;
                for(ServerPlayer player : serverLevel.players()){
                    if(blockEntity.border.inside(player.getPosition(1))){
                        BorderManager.applyIfAbsent(player, blockEntity.border);
                    }
                }
            }
        }
    }
}
