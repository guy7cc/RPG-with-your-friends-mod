package io.github.guy7cc.save.cap;

import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class PlayerMp implements INBTSerializable<CompoundTag> {
    protected float mp;
    protected float max;
    protected ServerPlayer player;
    public PlayerMp(ServerPlayer player) { this(player, 0, 20); }
    public PlayerMp(ServerPlayer player, float mp, float max) {
        this.mp = mp;
        this.max = max;
        this.player = player;
    }

    public void setMp(float mp) {
        this.mp = Math.max(0, Math.min(max, mp));
        syncToClient();
        syncToParty();
    }

    public void addMp(float mp) { setMp(this.mp + mp); }

    public float getMp() { return this.mp; }

    public void setMaxMp(float maxMp) {
        this.max = Math.max(maxMp, 0f);
        this.mp = Math.min(maxMp, this.mp);
        syncToClient();
        syncToParty();
    }

    public float getMaxMp() { return this.max; }

    public void syncToClient(){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(mp, max, player.getUUID()));
    }

    public void syncToParty(){
        Party party = PartyList.getInstance().getParty(player.getUUID());
        if(party != null){
            MinecraftServer server = player.getServer();
            for(UUID uuid : party.getMemberList()){
                ServerPlayer member = server.getPlayerList().getPlayer(uuid);
                if(member != null && !uuid.equals(player.getUUID())){
                    RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> member), new ClientboundSyncPlayerMpPacket(mp, max, player.getUUID()));
                }
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("PlayerMp", mp);
        nbt.putFloat("PlayerMaxMp", max);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.mp = nbt.contains("PlayerMp", Tag.TAG_FLOAT) ? nbt.getFloat("PlayerMp") : 0;
        this.max = nbt.contains("PlayerMaxMp", Tag.TAG_FLOAT) ? nbt.getFloat("PlayerMaxMp") : 20;
    }
}
