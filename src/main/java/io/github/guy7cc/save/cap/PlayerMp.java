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
    public PlayerMp(ServerPlayer player, float max) { this(player, 0, 20); }
    public PlayerMp(ServerPlayer player, float mp, float max) {
        this.mp = mp;
        this.max = max;
        this.player = player;
    }

    public void setMp(float mp) {
        this.mp = Math.max(0, Math.min(max, mp));
        syncMpToClient();
        syncMpToParty();
    }

    public void addMp(float mp) { setMp(this.mp + mp); }

    public float getMp() { return this.mp; }

    public void setMaxMp(float maxMp) {
        this.max = Math.max(maxMp, 0f);
        this.mp = Math.min(maxMp, this.mp);
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(maxMp, ClientboundSyncPlayerMpPacket.Type.MAX, player.getUUID()));
    }

    public float getMaxMp() { return this.max; }

    public void syncMpToClient(){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(mp, ClientboundSyncPlayerMpPacket.Type.VALUE, player.getUUID()));
    }

    public void syncMpToParty(){
        Party party = PartyList.getInstance().getParty(player.getUUID());
        if(party != null){
            MinecraftServer server = player.getServer();
            for(UUID uuid : party.getMemberList()){
                ServerPlayer member = server.getPlayerList().getPlayer(uuid);
                if(member != null && !uuid.equals(player.getUUID())){
                    RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> member), new ClientboundSyncPlayerMpPacket(mp, ClientboundSyncPlayerMpPacket.Type.VALUE, player.getUUID()));
                }
            }
        }
    }

    public void syncMaxMpToClient(){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(max, ClientboundSyncPlayerMpPacket.Type.MAX, player.getUUID()));
    }

    public void syncMaxMpToParty(){
        Party party = PartyList.getInstance().getParty(player.getUUID());
        if(party != null){
            MinecraftServer server = player.getServer();
            for(UUID uuid : party.getMemberList()){
                ServerPlayer member = server.getPlayerList().getPlayer(uuid);
                if(member != null && !uuid.equals(player.getUUID())){
                    RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> member), new ClientboundSyncPlayerMpPacket(max, ClientboundSyncPlayerMpPacket.Type.MAX, player.getUUID()));
                }
            }
        }
    }

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
