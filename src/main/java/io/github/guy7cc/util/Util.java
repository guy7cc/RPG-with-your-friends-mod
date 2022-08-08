package io.github.guy7cc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class Util {
    public static AABB loadAABB(CompoundTag tag){
        double minX = tag.getDouble("MinX");
        double minY = tag.getDouble("MinY");
        double minZ = tag.getDouble("MinZ");
        double maxX = tag.getDouble("MaxX");
        double maxY = tag.getDouble("MaxY");
        double maxZ = tag.getDouble("MaxZ");
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public static CompoundTag saveAABB(AABB aabb){
        CompoundTag tag = new CompoundTag();
        tag.putDouble("MinX", aabb.minX);
        tag.putDouble("MinY", aabb.minY);
        tag.putDouble("MinZ", aabb.minZ);
        tag.putDouble("MaxX", aabb.maxX);
        tag.putDouble("MaxY", aabb.maxY);
        tag.putDouble("MaxZ", aabb.maxZ);
        return tag;
    }
}
