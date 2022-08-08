package io.github.guy7cc.block;

import io.github.guy7cc.block.entity.BorderedRpgwSpawnerBlockEntity;
import io.github.guy7cc.block.entity.RpgwBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BorderedRpgwSpawnerBlock extends RpgwSpawnerBlock {
    public BorderedRpgwSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        AbstractBorderBlock.onRemove(this, pState, pLevel, pPos, pNewState, pIsMoving);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BorderedRpgwSpawnerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide) return pBlockEntityType == RpgwBlockEntities.BORDERED_RPGW_SPAWNER.get() ? (level, pos, state, blockEntity) -> BorderedRpgwSpawnerBlockEntity.clientTick(level, pos, state, (BorderedRpgwSpawnerBlockEntity) blockEntity) : null;
        else return pBlockEntityType == RpgwBlockEntities.BORDERED_RPGW_SPAWNER.get() ? (level, pos, state, blockEntity) -> BorderedRpgwSpawnerBlockEntity.serverTick(level, pos, state, (BorderedRpgwSpawnerBlockEntity) blockEntity) : null;
    }
}
