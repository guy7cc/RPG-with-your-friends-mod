package io.github.guy7cc.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.item.CoinItem;
import io.github.guy7cc.item.RpgwItems;
import io.github.guy7cc.save.cap.PropertyType;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.save.cap.RpgPlayerPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class PlayerMoneyOverlay {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/money_overlays.png");
    private static final ItemStack[] COINS = new ItemStack[]{
            new ItemStack(RpgwItems.IRON_COIN.get()),
            new ItemStack(RpgwItems.COPPER_COIN.get()),
            new ItemStack(RpgwItems.SILVER_COIN.get()),
            new ItemStack(RpgwItems.GOLD_COIN.get())
    };
    private static final int[] GRADIENT = new int[]{
            1,
            12,
            123,
            1234
    };

    private boolean initialized = false;
    private long displayMoney = 0;
    private int gradient = 0;

    public PlayerMoneyOverlay(){

    }

    public void tick(){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        RpgPlayerProperty p = RpgPlayerPropertyManager.get(player.getUUID());
        if(p == null) return;
        long money = p.getValue(PropertyType.MONEY);
        if(Math.abs(money - displayMoney) < Math.abs(gradient)){
            displayMoney = money;
            gradient = 0;
        } else {
            displayMoney += gradient;
        }
    }

    public void onChangeMoney(){
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        RpgPlayerProperty p = RpgPlayerPropertyManager.get(player.getUUID());
        if(p == null) return;
        long money = p.getValue(PropertyType.MONEY);
        if(!initialized){
            displayMoney = money;
            initialized = true;
            return;
        }
        if(displayMoney < money){
            for(int i = GRADIENT.length - 1; i >= 0; i--){
                if((money - displayMoney) / 12d > GRADIENT[i]){
                    gradient = GRADIENT[i];
                    break;
                }
            }
            if(gradient == 0) gradient = 1;
        } else if(displayMoney > money){
            for(int i = GRADIENT.length - 1; i >= 0; i--){
                if((displayMoney - money) / 12d > GRADIENT[i]){
                    gradient = -GRADIENT[i];
                    break;
                }
            }
            if(gradient == 0) gradient = -1;
        } else{
            displayMoney = money;
            gradient = 0;
        }
    }

    public void render(int screenWidth, int screenHeight, PoseStack poseStack){
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        RenderSystem.enableBlend();

        GuiComponent.blit(poseStack, screenWidth - 75, screenHeight - 14, 0, 0, 75, 14, 128, 128);

        int i = COINS.length - 1;
        for(; i >= 0; i--){
            if(displayMoney >= ((CoinItem)COINS[i].getItem()).getRank().value) break;
        }
        Minecraft.getInstance().getItemRenderer().renderGuiItem(COINS[Math.max(i, 0)], screenWidth - 76, screenHeight - 15);

        Font font = Minecraft.getInstance().font;
        String s = displayMoney + " " + RpgwMod.CURRENCY;
        float width = font.width(s);
        Minecraft.getInstance().font.drawShadow(poseStack, s, screenWidth - width - 2, screenHeight - 10.5f, 0xffffffff);

        RenderSystem.disableBlend();
    }

    public void reset(){
        initialized = false;
        displayMoney = 0;
        gradient = 0;
    }

    public void onClientLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        reset();
    }
}
