package io.github.guy7cc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public class RpgwIngameOverlay extends GuiComponent {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/overlays.png");

    private static int tickCount = 0;
    private static long healthBlinkTime = 0;
    private static float lastHealth = 0;
    private static float backBarHealthRate = 0;
    private static long backBarRunningTime = -100;

    public static IIngameOverlay PLAYER_HEALTH_BAR_ELEMENT;

    public static void registerOverlay(){
        PLAYER_HEALTH_BAR_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, "Player Health Bar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            if (!Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
            {
                gui.setupOverlayRenderState(true, false);
                renderHealthBar(screenWidth, screenHeight, poseStack);
            }
        });
    }

    public static void tick(){
        ++tickCount;
    }

    private static void renderHealthBar(int screenWidth, int screenHeight, PoseStack poseStack){
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        RenderSystem.enableBlend();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player)minecraft.getCameraEntity();

        float health = player.getHealth();
        AttributeInstance attrMaxHealth = minecraft.player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getValue();

        boolean highlight = healthBlinkTime > (long)tickCount && (healthBlinkTime - (long)tickCount) / 3L %2L == 1L;

        if(health != lastHealth) {
            backBarHealthRate = lastHealth / healthMax;
            backBarRunningTime = tickCount + 20;
        }

        if(health < lastHealth && player.invulnerableTime > 0){
            healthBlinkTime = tickCount + 20;
        } else if(health > lastHealth && player.invulnerableTime > 0){
            healthBlinkTime = tickCount + 10;
        }

        float healthRate = health / healthMax;
        if(tickCount > backBarRunningTime){
            if(Math.abs(backBarHealthRate - healthRate) <= 0.001f){
                backBarHealthRate = healthRate;
            } else if(backBarHealthRate > healthRate + 0.001f){
                backBarHealthRate -= 0.001f;
            } else if(backBarHealthRate < healthRate - 0.001f){
                backBarHealthRate -= 0.001f;
            }
        }


        lastHealth = health;

        int left = screenWidth / 2 - 81;
        int top = screenHeight - 35;
        blit(poseStack, left, top, 0, 0, 81, 5, 128, 64);
        blit(poseStack, left, top, 0, 15, (int)Math.ceil(81 * backBarHealthRate), 5, 128, 64);
        blit(poseStack, left, top, 0, 5 + (highlight ? 5 : 0), (int)Math.ceil(81 * healthRate), 5, 128, 64);
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1);
        String healthString = String.format("%.1f", health) + " / " + String.format("%.1f", healthMax);
        drawString(poseStack, minecraft.font, healthString, (left - 2 - minecraft.font.width(healthString) / 2) * 2, top * 2 + 1, 0xffffffff);
        poseStack.popPose();
        RenderSystem.disableBlend();
    }

}
