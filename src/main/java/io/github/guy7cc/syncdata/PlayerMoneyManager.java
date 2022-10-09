package io.github.guy7cc.syncdata;

import io.github.guy7cc.save.cap.PlayerMoney;
import io.github.guy7cc.save.cap.PlayerMoneyProvider;
import io.github.guy7cc.save.cap.PlayerMp;
import io.github.guy7cc.save.cap.PlayerMpProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerMoneyManager {
    public static long money;

    //server-side
    public static PlayerMoney getPlayerMoney(ServerPlayer player){
        return player.getCapability(PlayerMoneyProvider.PLAYER_MONEY_CAPABILITY).orElse(null);
    }

    public static void syncToClient(ServerPlayer player){
        PlayerMoney playerMoney = getPlayerMoney(player);
        if(playerMoney != null) playerMoney.syncToClient();
    }
}
