package io.github.guy7cc.block.entity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleBorderBlockEntity extends AbstractBorderBlockEntity{
    public ExampleBorderBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.EXAMPLE_BORDER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExampleBorderBlockEntity blockEntity){
        AbstractBorderBlockEntity.tick(level, pos, state, blockEntity);
    }
}
