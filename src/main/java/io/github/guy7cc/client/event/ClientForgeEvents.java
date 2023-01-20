package io.github.guy7cc.client.event;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.client.screen.party.PartyMenuScreen;
import io.github.guy7cc.resource.DimensionDataManager;
import io.github.guy7cc.sync.BorderManager;
import io.github.guy7cc.sync.PartyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        PartyManager.onClientLoggedOut(event);
        BorderManager.onClientLoggedOut(event);
        RpgwIngameOverlay.money.onClientLoggedOut(event);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        RpgwIngameOverlay.onClientTick(event);
    }

    @SubscribeEvent
    public static void onClientRespawn(ClientPlayerNetworkEvent.RespawnEvent event){
        BorderManager.onClientRespawn(event);
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenOpenEvent event){
        DimensionDataManager.onScreenOpen(event);
    }

    @SubscribeEvent
    public static void onInitScreenPre(ScreenEvent.InitScreenEvent.Post event){
        PartyMenuScreen.onInitScreenPre(event);
    }

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelLastEvent event){
        BorderManager.onRenderLevelLast(event);
    }
}
