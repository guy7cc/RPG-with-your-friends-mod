package io.github.guy7cc.block.entity;

import io.github.guy7cc.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RpgwBaseSpawner {
    protected List<Entity> entityList = new ArrayList<>();
    protected AABB spawnArea;
    protected AABB playerArea;
    protected AABB renderBoundingBox;

    public RpgwBaseSpawner(BlockPos pos){
        this.spawnArea = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
        this.playerArea = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
        this.renderBoundingBox = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3, pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4);
    }

    public RpgwBaseSpawner(CompoundTag tag){
        this.spawnArea = Util.loadAABB(tag.getCompound("SpawnArea"));
        this.playerArea = Util.loadAABB(tag.getCompound("PlayerArea"));
        this.renderBoundingBox = new AABB(
                Math.min(this.spawnArea.minX, this.playerArea.minX),
                Math.min(this.spawnArea.minY, this.playerArea.minY),
                Math.min(this.spawnArea.minZ, this.playerArea.minZ),
                Math.max(this.spawnArea.maxX, this.playerArea.maxX),
                Math.max(this.spawnArea.maxY, this.playerArea.maxY),
                Math.max(this.spawnArea.maxZ, this.playerArea.maxZ)
        );
    }

    public abstract void clientTick(Level pLevel, BlockPos pPos);

    public abstract void serverTick(ServerLevel pServerLevel, BlockPos pPos);

    public CompoundTag save(CompoundTag pTag){
        pTag.put("SpawnArea", Util.saveAABB(this.spawnArea));
        pTag.put("PlayerArea", Util.saveAABB(this.playerArea));
        return pTag;
    }

    protected boolean isNearPlayer(Level level){
        for(Player player : level.players()){
            if(playerArea.contains(player.position())) return true;
        }
        return false;
    }

    protected void summon(ServerLevel level, CompoundTag tag){
        Optional<EntityType<?>> optional = EntityType.by(tag);
        if(optional.isEmpty()) return;
        if (!optional.get().getCategory().isFriendly() && level.getDifficulty() == Difficulty.PEACEFUL) return;
        double x = 0;
        double y = 0;
        double z = 0;
        final int maxCount = 20;
        for(int i = 0; i < maxCount; i++){
            x = spawnArea.minX + (spawnArea.maxX - spawnArea.minX) * level.random.nextDouble();
            y = spawnArea.minY + (spawnArea.maxY - spawnArea.minY) * level.random.nextDouble();
            z = spawnArea.minZ + (spawnArea.maxZ - spawnArea.minZ) * level.random.nextDouble();
            if(level.noCollision(optional.get().getAABB(x, y, z))) break;
            else if(i == maxCount - 1) return;
        }
        BlockPos pos = new BlockPos(x, y, z);
        double finalX = x;
        double finalY = y;
        double finalZ = z;
        Entity entity = EntityType.loadEntityRecursive(tag, level, e -> {
            e.moveTo(finalX, finalY, finalZ, e.getYRot(), e.getXRot());
            return e;
        });
        if(entity == null) return;
        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
        if(!level.tryAddFreshEntityWithPassengers(entity)) return;
        level.levelEvent(2004, pos, 0);
        if(entity instanceof Mob) ((Mob)entity).spawnAnim();
    }

    public AABB getSpawnArea() {
        return this.spawnArea;
    }

    public void setSpawnArea(AABB aabb){
        this.spawnArea = aabb;
        this.renderBoundingBox = new AABB(
                Math.min(this.playerArea.minX, aabb.minX),
                Math.min(this.playerArea.minY, aabb.minY),
                Math.min(this.playerArea.minZ, aabb.minZ),
                Math.max(this.playerArea.maxX, aabb.maxX),
                Math.max(this.playerArea.maxY, aabb.maxY),
                Math.max(this.playerArea.maxZ, aabb.maxZ));
    }

    public AABB getPlayerArea() {
        return this.playerArea;
    }

    public void setPlayerArea(AABB aabb){
        this.playerArea = aabb;
        this.renderBoundingBox = new AABB(
                Math.min(this.spawnArea.minX, aabb.minX),
                Math.min(this.spawnArea.minY, aabb.minY),
                Math.min(this.spawnArea.minZ, aabb.minZ),
                Math.max(this.spawnArea.maxX, aabb.maxX),
                Math.max(this.spawnArea.maxY, aabb.maxY),
                Math.max(this.spawnArea.maxZ, aabb.maxZ));
    }

    public abstract Type getType();

    public static class Single extends RpgwBaseSpawner{
        private CompoundTag entityToSpawn;
        private int tickCount = 0;
        public Single(BlockPos pos){
            super(pos);
            this.entityToSpawn = new CompoundTag();
            this.entityToSpawn.putString("id", "minecraft:pig");
        }

        public Single(CompoundTag tag){
            super(tag);
            if(tag.contains("EntityToSpawn")){
                this.entityToSpawn = tag.getCompound("EntityToSpawn");
            } else {
                this.entityToSpawn = new CompoundTag();
                this.entityToSpawn.putString("id", "minecraft:pig");
            }
        }

        @Override
        public void clientTick(Level pLevel, BlockPos pPos) {

        }

        @Override
        public void serverTick(ServerLevel pServerLevel, BlockPos pPos) {
            tickCount++;
            if(tickCount % 60 == 0 && isNearPlayer(pServerLevel)){
                summon(pServerLevel, this.entityToSpawn);
            }
        }

        @Override
        public CompoundTag save(CompoundTag pTag) {
            pTag.put("EntityToSpawn", this.entityToSpawn);
            return super.save(pTag);
        }

        @Override
        public Type getType(){
            return Type.SINGLE;
        }
    }

    public enum Type{
        SINGLE(0);

        private int id;
        Type(int id){
            this.id = id;
        }

        public int getId(){ return this.id; }

        public static Type byId(int id){
            for(Type type : values()){
                if(type.id == id) return type;
            }
            return null;
        }
    }
}
