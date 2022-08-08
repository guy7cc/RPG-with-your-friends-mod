package io.github.guy7cc.block.entity;

import io.github.guy7cc.rpg.Border;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BorderedRpgwSpawnerBlockEntity extends RpgwSpawnerBlockEntity implements IBorderBlockEntity {
    private Border border;
    private int tickCount;

    public BorderedRpgwSpawnerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(RpgwBlockEntities.BORDERED_RPGW_SPAWNER.get(), pWorldPosition, pBlockState);
        this.border = new Border(pWorldPosition.getX() - 3, pWorldPosition.getX() + 4, pWorldPosition.getZ() - 3, pWorldPosition.getZ() + 4);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BorderedRpgwSpawnerBlockEntity blockEntity){
        RpgwSpawnerBlockEntity.clientTick(level, pos, state, blockEntity);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BorderedRpgwSpawnerBlockEntity blockEntity){
        RpgwSpawnerBlockEntity.serverTick(level, pos, state, blockEntity);
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
