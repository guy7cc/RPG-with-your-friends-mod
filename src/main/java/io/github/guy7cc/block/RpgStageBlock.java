package io.github.guy7cc.block;

import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.client.screen.RpgStageScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RpgStageBlock extends HorizontalDirectionalBlock implements EntityBlock {
    protected static final VoxelShape COLLISION_SHAPE_X = Shapes.or(
            Block.box(2, 0, 0, 14, 1, 16),
            Block.box(3, 1, 1, 13, 2, 15),
            Block.box(4, 2, 2, 12, 3, 14),
            Block.box(5, 3, 3, 11, 4, 13)
    );
    protected static final VoxelShape COLLISION_SHAPE_Z = Shapes.or(
            Block.box(0, 0, 2, 16, 1, 14),
            Block.box(1, 1, 3, 15, 2, 13),
            Block.box(2, 2, 4, 14, 3, 12),
            Block.box(3, 3, 5, 13, 4, 11)
    );
    protected static final VoxelShape SHAPE_X = Shapes.or(
            Block.box(2, 0, 0, 14, 1, 16),
            Block.box(3, 1, 1, 13, 2, 15),
            Block.box(4, 2, 2, 12, 3, 14),
            Block.box(5, 3, 3, 11, 4, 13),
            Block.box(6, 4, 4, 10, 28, 12)
    );
    protected static final VoxelShape SHAPE_Z = Shapes.or(
            Block.box(0, 0, 2, 16, 1, 14),
            Block.box(1, 1, 3, 15, 2, 13),
            Block.box(2, 2, 4, 14, 3, 12),
            Block.box(3, 3, 5, 13, 4, 11),
            Block.box(4, 4, 6, 12, 28, 10)
    );


    public RpgStageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING).getAxis() == Direction.Axis.X ? COLLISION_SHAPE_X : COLLISION_SHAPE_Z;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof RpgStageBlockEntity be){
            Minecraft.getInstance().setScreen(new RpgStageScreen(be));
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RpgStageBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide) return (level, pos, state, blockEntity) -> RpgStageBlockEntity.clientTick(level, pos, state, (RpgStageBlockEntity)blockEntity);
        else return null;
    }
}
