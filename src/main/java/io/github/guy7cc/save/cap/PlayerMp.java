package io.github.guy7cc.save.cap;

import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

public class PlayerMp implements INBTSerializable<CompoundTag> {
    protected float mp;
    protected float max;
    public PlayerMp(float max) { this(0, 20); }
    public PlayerMp(float mp, float max) {
        this.mp = mp;
        this.max = max;
    }

    public void setMp(float mp) {
        this.mp = Math.max(0, Math.min(max, mp));
    }
    public float getMp() { return this.mp; }
    public void addMp(float mp) { setMp(this.mp + mp); }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("PlayerMp", mp);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.mp = nbt.contains("PlayerMp", Tag.TAG_FLOAT) ? nbt.getFloat("PlayerMp") : 0;
    }
}
