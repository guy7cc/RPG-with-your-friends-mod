package io.github.guy7cc.block.entity;

import io.github.guy7cc.rpg.Border;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExampleBorderBlockEntity extends BlockEntity implements IBorderBlockEntity {
    private Border border;

    private int tickCount;

    public ExampleBorderBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.EXAMPLE_BORDER.get(), pWorldPosition, pBlockState);
        this.border = new Border(pWorldPosition.getX() - 3, pWorldPosition.getX() + 4, pWorldPosition.getZ() - 3, pWorldPosition.getZ() + 4);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExampleBorderBlockEntity blockEntity){
        blockEntity.tickCount++;
        blockEntity.tickBorder(blockEntity.tickCount, level, pos, state, blockEntity);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        loadBorder(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        saveBorder(pTag);
    }

    @Override
    public Border getBorder() {
        return this.border;
    }

    @Override
    public void setBorder(Border border) {
        this.border = border;
    }
}
