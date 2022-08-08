package io.github.guy7cc.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RpgwSpawnerBlockEntity extends BlockEntity {
    protected RpgwBaseSpawner baseSpawner;

    public RpgwSpawnerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        this(RpgwBlockEntities.RPGW_SPAWNER.get(), pWorldPosition, pBlockState);
    }

    public RpgwSpawnerBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState){
        super(pType, pWorldPosition, pBlockState);
        this.baseSpawner = new RpgwBaseSpawner.Single(pWorldPosition);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return this.baseSpawner.renderBoundingBox;
    }

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, RpgwSpawnerBlockEntity pBlockEntity){
        pBlockEntity.baseSpawner.clientTick(pLevel, pPos);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RpgwSpawnerBlockEntity pBlockEntity){
        pBlockEntity.baseSpawner.serverTick((ServerLevel) pLevel, pPos);
    }

    public RpgwBaseSpawner getBaseSpawner() {
        return baseSpawner;
    }
}
