package io.github.guy7cc.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.syncdata.PartyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RpgwIngameOverlay{
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

    public static IIngameOverlay PLAYER_STATUS_ELEMENT;
    public static IIngameOverlay PARTY_STATUS_ELEMENT;

    private static PartyMemberStatusRenderer localStatus;
    private static List<PartyMemberStatusRenderer> partyStatusList = new ArrayList<>();

    public static void registerOverlay(){
        /*
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
         */
        localStatus = new PartyMemberStatusRenderer(null, true);
        localStatus.needUpdatePlayer = false;
        PLAYER_STATUS_ELEMENT = OverlayRegistry.registerOverlayTop("Player Status", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            if (!Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements()) {
                localStatus.render(screenWidth, screenHeight, poseStack, tickCount, screenWidth / 2 - 81, screenHeight - 41);
            }
        });
        PARTY_STATUS_ELEMENT = OverlayRegistry.registerOverlayTop("Party Status", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            if (!Minecraft.getInstance().options.hideGui && gui.shouldDrawSurvivalElements()) {
                int height = 12;
                Minecraft minecraft = Minecraft.getInstance();
                ClientPacketListener connection = minecraft.getConnection();
                boolean flag = minecraft.isLocalServer() || connection.getConnection().isEncrypted();

                for (PartyMemberStatusRenderer renderer : partyStatusList) {
                    if (!renderer.nullPlayer()) {
                        PlayerInfo info = connection.getPlayerInfo(renderer.uuid);
                        if (flag && info != null) {
                            RenderSystem.setShaderTexture(0, info.getSkinLocation());
                            GuiComponent.blit(poseStack, 1, screenHeight - height + 2, 8, 8, 8.0F, 8, 8, 8, 64, 64);
                            GuiComponent.blit(poseStack, 1, screenHeight - height + 2, 8, 8, 40.0F, 8, 8, 8, 64, 64);
                        }
                        renderer.render(screenWidth, screenHeight, poseStack, tickCount, 40, screenHeight - height);
                        height += 12;
                    }
                }
            }
        });
    }

    public static void tick(){
        ++tickCount;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player)minecraft.getCameraEntity();
        localStatus.setPlayer(player);
        localStatus.tick(tickCount);
        partyStatusList.forEach(renderer -> renderer.tick(tickCount));
    }

    public static void refreshPartyStatus(){
        if(PartyManager.clientParty == null){
            partyStatusList.clear();
        } else {
            partyStatusList.clear();
            LocalPlayer local = Minecraft.getInstance().player;
            for(UUID uuid : PartyManager.clientParty.getMemberList()){
                if(local == null || !local.getUUID().equals(uuid))
                    partyStatusList.add(new PartyMemberStatusRenderer(uuid, false));
            }
        }
    }
}
