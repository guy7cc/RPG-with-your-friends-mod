package io.github.guy7cc.block;

import io.github.guy7cc.block.entity.BorderedRpgwSpawnerBlockEntity;
import io.github.guy7cc.block.entity.RpgwBaseSpawner;
import io.github.guy7cc.block.entity.RpgwBlockEntities;
import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RpgwSpawnerBlock extends AbstractGlassBlock implements EntityBlock {
    protected RpgwSpawnerBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if(itemStack.getItem() instanceof SpawnEggItem item){
            if(pLevel.isClientSide) return InteractionResult.SUCCESS;
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof RpgwSpawnerBlockEntity){
                RpgwBaseSpawner baseSpawner = ((RpgwSpawnerBlockEntity) blockEntity).getBaseSpawner();
                if(baseSpawner instanceof RpgwBaseSpawner.Single single){
                    EntityType<?> entityType = item.getType(itemStack.getTag());
                    single.setEntityId(entityType);
                    blockEntity.setChanged();
                    pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                    itemStack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RpgwSpawnerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide) return pBlockEntityType == RpgwBlockEntities.RPGW_SPAWNER.get() ? (level, pos, state, blockEntity) -> RpgwSpawnerBlockEntity.clientTick(level, pos, state, (RpgwSpawnerBlockEntity) blockEntity) : null;
        else return pBlockEntityType == RpgwBlockEntities.RPGW_SPAWNER.get() ? (level, pos, state, blockEntity) -> RpgwSpawnerBlockEntity.serverTick(level, pos, state, (RpgwSpawnerBlockEntity) blockEntity) : null;
    }
}
