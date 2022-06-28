package io.github.guy7cc.save.cap;

import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

public class PlayerMp implements INBTSerializable<CompoundTag> {
    protected float mp;
    protected float max;
    protected ServerPlayer player;
    public PlayerMp(ServerPlayer player, float max) { this(player, 0, 20); }
    public PlayerMp(ServerPlayer player, float mp, float max) {
        this.mp = mp;
        this.max = max;
        this.player = player;
    }

    public void setMp(float mp) {
        this.mp = Math.max(0, Math.min(max, mp));
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(mp, ClientboundSyncPlayerMpPacket.Type.VALUE));
    }

    public void addMp(float mp) { setMp(this.mp + mp); }

    public float getMp() { return this.mp; }

    public void setMaxMp(float maxMp) {
        this.max = Math.max(maxMp, 0f);
        this.mp = Math.min(maxMp, this.mp);
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(maxMp, ClientboundSyncPlayerMpPacket.Type.MAX));
    }

    public float getMaxMp() { return this.max; }

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