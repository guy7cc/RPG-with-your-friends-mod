package io.github.guy7cc.rpg;

import net.minecraft.world.phys.Vec3;

public class Border {
    public int id;
    public double minX;
    public double maxX;
    public double minZ;
    public double maxZ;

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
}
