package io.github.guy7cc.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class PlayerMoneyOverlay {
    private long startMoney = 0;
    private int tick = 20;

    public PlayerMoneyOverlay(){

    }

    public void tick(){
        if(tick > 0) tick--;
    }

    public void onChangeMoney(long money){
        this.startMoney = money;
        tick = 20;
    }

    public void render(int screenWidth, int screenHeight, PoseStack poseStack){
        Font font = Minecraft.getInstance().font;
        String s = String.valueOf(getDisplayMoney());
        float width = font.width(s);
        Minecraft.getInstance().font.draw(poseStack, s, screenWidth - width - 5, screenHeight - 10, 0xffffffff);
    }

    private long getDisplayMoney(){
        if(tick == 0){
            return PlayerMoneyManager.getPlayerMoney();
        } else {
            return (long)((startMoney * tick + PlayerMoneyManager.getPlayerMoney() * (20 - startMoney)) / 20d);
        }
    }
}
