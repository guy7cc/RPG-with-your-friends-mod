package io.github.guy7cc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
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
    private static float backgroundHealthRate = 0;
    private static long backgroundHealthRunningTime = -100;
    private static long mpBlinkTime = 0;
    private static float lastMp = 0;
    private static float backgroundMpRate = 0;
    private static long backgroundMpRunningTime = -100;

    public static IIngameOverlay PLAYER_HEALTH_BAR_ELEMENT;
    public static IIngameOverlay PLAYER_MP_BAR_ELEMENT;

    public static void registerOverlay(){
        PLAYER_HEALTH_BAR_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, "Player Health Bar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            if (!Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
            {
                gui.setupOverlayRenderState(true, false);
                renderHealthBar(screenWidth, screenHeight, poseStack);
            }
        });
        PLAYER_MP_BAR_ELEMENT = OverlayRegistry.registerOverlayAbove(PLAYER_HEALTH_BAR_ELEMENT, "Player Mp Bar", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            if (!Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements())
            {
                gui.setupOverlayRenderState(true, false);
                renderMpBar(screenWidth, screenHeight, poseStack);
            }
        });
    }

    public static void tick(){
        ++tickCount;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player)minecraft.getCameraEntity();
        if(player != null){
            float health = player.getHealth();
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float)attrMaxHealth.getValue();
            float healthRate = health / healthMax;
            if(tickCount > backgroundHealthRunningTime){
                if(Math.abs(backgroundHealthRate - healthRate) <= 0.01f){
                    backgroundHealthRate = healthRate;
                } else if(backgroundHealthRate > healthRate + 0.01f){
                    backgroundHealthRate -= 0.01f;
                } else if(backgroundHealthRate < healthRate - 0.01f){
                    backgroundHealthRate -= 0.01f;
                }
            }
        }

        float mp = PlayerMpManager.clientPlayerMp;
        float mpMax = PlayerMpManager.clientPlayerMpMax;
        float mpRate = mp / mpMax;
        if(tickCount > backgroundMpRunningTime){
            if(Math.abs(backgroundMpRate - mpRate) <= 0.01f){
                backgroundMpRate = mpRate;
            } else if(backgroundMpRate > 0.01f){
                backgroundMpRate -= 0.01f;
            } else if(backgroundMpRate < mpRate - 0.01f){
                backgroundMpRate -= 0.01f;
            }
        }
    }

    private static void renderHealthBar(int screenWidth, int screenHeight, PoseStack poseStack){
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        RenderSystem.enableBlend();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player)minecraft.getCameraEntity();

        float health = player.getHealth();
        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float)attrMaxHealth.getValue();

        boolean highlight = healthBlinkTime > (long)tickCount && (healthBlinkTime - (long)tickCount) / 3L %2L == 1L;

        if(health != lastHealth) {
            backgroundHealthRate = lastHealth / healthMax;
            backgroundHealthRunningTime = tickCount + 20;
        }

        if(health < lastHealth && player.invulnerableTime > 0){
            healthBlinkTime = tickCount + 20;
        } else if(health > lastHealth && player.invulnerableTime > 0){
            healthBlinkTime = tickCount + 10;
        }

        float healthRate = health / healthMax;

        lastHealth = health;

        int left = screenWidth / 2 - 81;
        int top = screenHeight - 35;
        blit(poseStack, left, top, 0, 0, 81, 5, 128, 64);
        blit(poseStack, left, top, 0, 15, (int)Math.ceil(81 * backgroundHealthRate), 5, 128, 64);
        blit(poseStack, left, top, 0, 5 + (highlight ? 5 : 0), (int)Math.ceil(81 * healthRate), 5, 128, 64);
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1);
        String healthString = String.format("%.1f", health) + " / " + String.format("%.1f", healthMax);
        drawString(poseStack, minecraft.font, healthString, (left - 2 - minecraft.font.width(healthString) / 2) * 2, top * 2 + 1, 0xffffffff);
        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    private static void renderMpBar(int screenWidth, int screenHeight, PoseStack poseStack){
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        RenderSystem.enableBlend();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player)minecraft.getCameraEntity();

        float mp = PlayerMpManager.clientPlayerMp;
        float mpMax = PlayerMpManager.clientPlayerMpMax;

        boolean highlight = mpBlinkTime > (long)tickCount && (mpBlinkTime - (long)tickCount) / 3L %2L == 1L;

        if(mp != lastMp) {
            backgroundMpRate = lastMp / mpMax;
            backgroundMpRunningTime = tickCount + 20;
        }

        if(mp < lastMp){
            mpBlinkTime = tickCount + 20;
        } else if(mp > lastMp){
            mpBlinkTime = tickCount + 10;
        }

        float mpRate = mp / mpMax;

        lastMp = mp;

        int left = screenWidth / 2 - 81;
        int top = screenHeight - 41;
        blit(poseStack, left, top, 0, 20, 81, 5, 128, 64);
        blit(poseStack, left, top, 0, 35, (int)Math.ceil(81 * backgroundMpRate), 5, 128, 64);
        blit(poseStack, left, top, 0, 25 + (highlight ? 5 : 0), (int)Math.ceil(81 * mpRate), 5, 128, 64);
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1);
        String mpString = String.format("%.1f", mp) + " / " + String.format("%.1f", mpMax);
        drawString(poseStack, minecraft.font, mpString, (left - 2 - minecraft.font.width(mpString) / 2) * 2, top * 2 + 1, 0xffffffff);
        poseStack.popPose();
        RenderSystem.disableBlend();
    }
}
