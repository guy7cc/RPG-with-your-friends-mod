package io.github.guy7cc.syncdata;

import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.save.cap.PlayerMp;
import io.github.guy7cc.save.cap.PlayerMpCapabilityProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PlayerMpManager {
    public static float clientPlayerMp = 20;
    public static float clientPlayerMpMax = 20;

    public static PlayerMp getPlayerMp(ServerPlayer player){
        return player.getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY).orElse(null);
    }

    public static void syncMpToClient(ServerPlayer player){
        float mp = player.getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY)
                .map(PlayerMp::getMp)
                .orElse(0f);
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(mp, ClientboundSyncPlayerMpPacket.Type.VALUE));
    }

    public static void syncMaxMpToClient(ServerPlayer player){
        float max = player.getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY)
                .map(PlayerMp::getMaxMp)
                .orElse(0f);
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMpPacket(max, ClientboundSyncPlayerMpPacket.Type.MAX));
    }

    public static void serverTick(ServerLevel level){

    }
}
