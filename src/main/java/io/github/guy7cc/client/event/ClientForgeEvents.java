package io.github.guy7cc.client.event;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.client.screen.party.PartyMenuScreen;
import io.github.guy7cc.syncdata.BorderManager;
import io.github.guy7cc.syncdata.PartyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        PartyManager.clientParty = null;
        BorderManager.clientBorder = null;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START) RpgwIngameOverlay.tick();
    }

    @SubscribeEvent
    public static void onClientRespawn(ClientPlayerNetworkEvent.RespawnEvent event){
        BorderManager.clientBorder = null;
    }

    public static final ResourceLocation PARTY_MENU_BUTTON_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/party_menu_button.png");

    @SubscribeEvent
    public static void onInitScreenPre(ScreenEvent.InitScreenEvent.Post event){
        if(event.getScreen() instanceof InventoryScreen screen){
            event.addListener(new ImageButton(screen.getGuiLeft() + screen.getXSize() + 1, screen.getGuiTop() + screen.getYSize() - 20, 20, 20, 0, 0, 20, PARTY_MENU_BUTTON_LOCATION, 32, 64, button -> {
                Minecraft.getInstance().setScreen(new PartyMenuScreen());
            }));
        }
    }

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelLastEvent event){
        BorderManager.renderBorder(event.getPoseStack());
    }
}
