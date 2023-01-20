package io.github.guy7cc.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.sync.PlayerMpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PartyMemberStatusOverlay {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/overlays.png");

    private float health = 0;
    private float healthMax = 20;
    private float mp = 0;
    private float mpMax = 20;

    private long healthBlinkTime = 0;
    private float lastHealth = 0;
    private float backgroundHealthRate = 0;
    private long backgroundHealthRunningTime = -100;
    private long mpBlinkTime = 0;
    private float lastMp = 0;
    private float backgroundMpRate = 0;
    private long backgroundMpRunningTime = -100;

    public boolean needUpdatePlayer = true;

    public UUID uuid;
    private Player player;
    private boolean local;

    public PartyMemberStatusOverlay(UUID uuid){
        this(uuid, false);
    }

    public PartyMemberStatusOverlay(UUID uuid, boolean local){
        this.uuid = uuid;
        this.local = local;
    }

    public void tick(int tickCount){
        if(needUpdatePlayer && tickCount % 10 == 0){
            player = getPlayer();
        }
        if(player != null){
            try{
                health = player.getHealth();
                AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
                healthMax = (float)attrMaxHealth.getValue();

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

                mp = PlayerMpManager.getPlayerMp(player.getUUID());
                mpMax = PlayerMpManager.getPlayerMaxMp(player.getUUID());
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
            } catch(Throwable throwable) {
                RpgwMod.LOGGER.info(throwable);
            }
        }
    }

    public void render(int screenWidth, int screenHeight, PoseStack poseStack, int tickCount, int left, int top){
        beforeRender();
        renderMpBar(screenWidth, screenHeight, poseStack, tickCount, left, top);
        afterRender();
        beforeRender();
        renderHealthBar(screenWidth, screenHeight, poseStack, tickCount, left, top + 6);
        afterRender();
    }

    private static void beforeRender() {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        RenderSystem.enableBlend();
    }

    private static void afterRender() {
        RenderSystem.disableBlend();
    }

    private void renderHealthBar(int screenWidth, int screenHeight, PoseStack poseStack, int tickCount, int left, int top){
        Minecraft minecraft = Minecraft.getInstance();

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

        int u = local ? 0 : 81;
        int width = local ? 81 : 41;
        GuiComponent.blit(poseStack, left, top, u, 0, width, 5, 128, 64);
        GuiComponent.blit(poseStack, left, top, u, 15, (int)Math.ceil(width * backgroundHealthRate), 5, 128, 64);
        GuiComponent.blit(poseStack, left, top, u, 5 + (highlight ? 5 : 0), (int)Math.ceil(width * healthRate), 5, 128, 64);
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1);
        String healthString = String.format("%.1f", health) + " / " + String.format("%.1f", healthMax);
        GuiComponent.drawString(poseStack, minecraft.font, healthString, (left - 2 - minecraft.font.width(healthString) / 2) * 2, top * 2 + 1, 0xffffffff);
        poseStack.popPose();
    }

    private void renderMpBar(int screenWidth, int screenHeight, PoseStack poseStack, int tickCount, int left, int top){
        Minecraft minecraft = Minecraft.getInstance();

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

        int u = local ? 0 : 81;
        int width = local ? 81 : 41;
        GuiComponent.blit(poseStack, left, top, u, 20, width, 5, 128, 64);
        GuiComponent.blit(poseStack, left, top, u, 35, (int)Math.ceil(width * backgroundMpRate), 5, 128, 64);
        GuiComponent.blit(poseStack, left, top, u, 25 + (highlight ? 5 : 0), (int)Math.ceil(width * mpRate), 5, 128, 64);
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1);
        String mpString = String.format("%.1f", mp) + " / " + String.format("%.1f", mpMax);
        GuiComponent.drawString(poseStack, minecraft.font, mpString, (left - 2 - minecraft.font.width(mpString) / 2) * 2, top * 2 + 1, 0xffffffff);
        poseStack.popPose();
    }

    public Player getPlayer(){
        ClientLevel level = Minecraft.getInstance().level;
        if(level != null){
            for(Player player : level.players()){
                if(player.getUUID().equals(this.uuid)) return player;
            }
        }
        return null;
    }

    public void setPlayer(Player player){
        this.player = player;
        this.uuid = player != null ? player.getUUID() : null;
    }

    public boolean nullPlayer() { return player == null; }
}
