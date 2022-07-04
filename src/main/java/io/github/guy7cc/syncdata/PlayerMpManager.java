package io.github.guy7cc.syncdata;

import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.save.cap.PlayerMp;
import io.github.guy7cc.save.cap.PlayerMpCapabilityProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMpManager {
    private static final Map<UUID, PlayerMpManager> map = new HashMap<>();

    public static final float DEFAULT_MAX_MP = 20f;

    public float mp;
    public float maxMp;

    private PlayerMpManager(float mp, float maxMp){
        this.mp = mp;
        this.maxMp = maxMp;
    }

    //client-side
    public static float getPlayerMp(UUID uuid){
        return map.containsKey(uuid) ? map.get(uuid).mp : 0f;
    }

    public static void setPlayerMp(UUID uuid, float mp){
        if(map.containsKey(uuid)) map.get(uuid).mp = mp;
        else map.put(uuid, new PlayerMpManager(mp, DEFAULT_MAX_MP));
    }

    public static float getPlayerMaxMp(UUID uuid){
        return map.containsKey(uuid) ? map.get(uuid).maxMp : DEFAULT_MAX_MP;
    }

    public static void setPlayerMaxMp(UUID uuid, float maxMp){
        if(map.containsKey(uuid)) map.get(uuid).maxMp = maxMp;
        else map.put(uuid, new PlayerMpManager(0f, maxMp));
    }

    //server-side
    public static PlayerMp getPlayerMp(ServerPlayer player){
        return player.getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY).orElse(null);
    }

    public static void syncMpToClient(ServerPlayer player){
        PlayerMp playerMp = getPlayerMp(player);
        if(playerMp != null) playerMp.syncMpToClient();
    }

    public static void syncMpToParty(ServerPlayer player){
        PlayerMp playerMp = getPlayerMp(player);
        if(playerMp != null) playerMp.syncMpToParty();
    }

    public static void syncMaxMpToClient(ServerPlayer player){
        PlayerMp playerMp = getPlayerMp(player);
        if(playerMp != null) playerMp.syncMaxMpToClient();
    }

    public static void syncMaxMpToParty(ServerPlayer player){
        PlayerMp playerMp = getPlayerMp(player);
        if(playerMp != null) playerMp.syncMaxMpToParty();
    }

    public static void serverTick(ServerLevel level){

    }
}
