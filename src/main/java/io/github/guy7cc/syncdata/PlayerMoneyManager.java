package io.github.guy7cc.syncdata;

import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.save.cap.PlayerMoney;
import io.github.guy7cc.save.cap.PlayerMoneyProvider;
import net.minecraft.server.level.ServerPlayer;

public class PlayerMoneyManager {
    private static long money;

    //client-side
    public static long getPlayerMoney(){
        return money;
    }

    public static void setPlayerMoney(long money){
        PlayerMoneyManager.money = money;
        RpgwIngameOverlay.money.onChangeMoney();
    }

    //server-side
    public static PlayerMoney getPlayerMoneyCap(ServerPlayer player){
        return player.getCapability(PlayerMoneyProvider.PLAYER_MONEY_CAPABILITY).orElse(null);
    }

    public static void syncToClient(ServerPlayer player){
        PlayerMoney playerMoney = getPlayerMoneyCap(player);
        if(playerMoney != null) playerMoney.syncToClient();
    }
}
