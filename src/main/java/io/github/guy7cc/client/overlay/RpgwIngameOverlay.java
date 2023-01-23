package io.github.guy7cc.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.rpg.PartyManager;
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
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RpgwIngameOverlay{
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/overlays.png");

    private static int tickCount = 0;

    public static IIngameOverlay PLAYER_STATUS_ELEMENT;
    public static IIngameOverlay PARTY_STATUS_ELEMENT;
    public static IIngameOverlay PLAYER_MONEY_ELEMENT;

    public static final PartyMemberStatusOverlay localStatus = new PartyMemberStatusOverlay(null, true);
    public static final List<PartyMemberStatusOverlay> partyStatusList = new ArrayList<>();
    public static final PlayerMoneyOverlay money = new PlayerMoneyOverlay();

    public static void registerOverlay(){
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

                for (PartyMemberStatusOverlay renderer : partyStatusList) {
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
        PLAYER_MONEY_ELEMENT = OverlayRegistry.registerOverlayTop("Player Money", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            money.render(screenWidth, screenHeight, poseStack);
        });
    }

    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            ++tickCount;
            Minecraft minecraft = Minecraft.getInstance();
            Player player = (Player)minecraft.getCameraEntity();
            localStatus.setPlayer(player);
            localStatus.tick(tickCount);
            partyStatusList.forEach(renderer -> renderer.tick(tickCount));
            money.tick();
        }
    }

    public static void refreshPartyStatus(){
        if(PartyManager.clientParty == null){
            partyStatusList.clear();
        } else {
            partyStatusList.clear();
            LocalPlayer local = Minecraft.getInstance().player;
            for(UUID uuid : PartyManager.clientParty.getMemberList()){
                if(local == null || !local.getUUID().equals(uuid))
                    partyStatusList.add(new PartyMemberStatusOverlay(uuid, false));
            }
        }
    }
}
