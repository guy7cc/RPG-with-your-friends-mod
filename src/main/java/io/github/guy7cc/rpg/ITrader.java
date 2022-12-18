package io.github.guy7cc.rpg;

import io.github.guy7cc.client.screen.TraderScreen;
import io.github.guy7cc.resource.TraderData;
import net.minecraft.client.Minecraft;

public interface ITrader {
    TraderData getTraderData();

    void setTraderData(TraderData data);

    default void setTraderScreen(){
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new TraderScreen(this));
    }
}
