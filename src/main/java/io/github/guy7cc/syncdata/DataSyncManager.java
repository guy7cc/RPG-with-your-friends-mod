package io.github.guy7cc.syncdata;

import net.minecraft.server.level.ServerPlayer;

public class DataSyncManager {
    public static void syncLogIn(ServerPlayer player){
        PlayerMpManager.syncMpToClient(player);
        PlayerMpManager.syncMaxMpToClient(player);
    }
    public static void manageLogOut(ServerPlayer player){
        PartyManager.clientParty = null;
    }
}
