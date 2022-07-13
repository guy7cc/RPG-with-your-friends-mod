package io.github.guy7cc.rpg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

public class Border implements INBTSerializable<CompoundTag> {
    private static int borderIdCount = 0;

    public int id;
    public double minX;
    public double maxX;
    public double minZ;
    public double maxZ;

    public Border(){
        this(borderIdCount++, 0, 0, 0, 0);
    }

    public Border(double minX, double maxX, double minZ, double maxZ){
        this(borderIdCount++, minX, maxX, minZ, maxZ);
    }

    public Border(int id, double minX, double maxX, double minZ, double maxZ) {
        this.id = id;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public boolean inside(Vec3 pos){
       return minX <= pos.x && pos.x <= maxX && minZ <= pos.z && pos.z <= maxZ;
    }

    public boolean outsideEnough(Vec3 pos){
        return pos.x <= minX - 1 || maxX + 1 <= pos.x || pos.z <= minZ - 1 || maxZ + 1 <= pos.z;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("MinX", this.minX);
        tag.putDouble("MaxX", this.maxX);
        tag.putDouble("MinZ", this.minZ);
        tag.putDouble("MaxZ", this.maxZ);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.minX = nbt.getDouble("MinX");
        this.maxX = nbt.getDouble("MaxX");
        this.minZ = nbt.getDouble("MinZ");
        this.maxZ = nbt.getDouble("MaxZ");
    }
}
