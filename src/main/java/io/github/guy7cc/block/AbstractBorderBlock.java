package io.github.guy7cc.block;

import io.github.guy7cc.block.entity.IBorderBlockEntity;
import io.github.guy7cc.rpg.BorderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBorderBlock extends BaseEntityBlock {

    public AbstractBorderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(!pLevel.isClientSide && pState.getBlock() != pNewState.getBlock()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(entity instanceof IBorderBlockEntity borderBE){
                BorderManager.remove(serverLevel.getServer(), borderBE.getBorder().id);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public static void onRemove(Block block, BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(!pLevel.isClientSide && pState.getBlock() != pNewState.getBlock()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(entity instanceof IBorderBlockEntity borderBE){
                BorderManager.remove(serverLevel.getServer(), borderBE.getBorder().id);
            }
        }
    }
}
