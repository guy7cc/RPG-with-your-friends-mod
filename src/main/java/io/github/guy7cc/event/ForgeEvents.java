package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.resource.*;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.*;
import io.github.guy7cc.rpg.BorderManager;
import io.github.guy7cc.save.cap.RpgPlayerPropertyManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event){
        event.addListener(DimensionDataManager.instance);
        event.addListener(RpgStageManager.instance);
        event.addListener(RpgScenarioManager.instance);
        event.addListener(TraderDataManager.instance);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        JoinRequestCommand.register(dispatcher);
        RpgwDebugCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        RpgPlayerPropertyProvider.onAttachCapabilities(event);
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event){
        BorderManager.onEntityTravelToDimension(event);
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        event.getOriginal().reviveCaps();
        RpgPlayerPropertyProvider.onPlayerCloned(event);
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        RpgPlayerPropertyManager.onPlayerTick(event);
        BorderManager.onPlayerTick(event);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        RpgPlayerPropertyManager.onPlayerLoggedIn(event);
        PartyList.onPlayerLoggedIn(event);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        RpgPlayerPropertyManager.onPlayerLoggedOut(event);
        PartyList.onPlayerLoggedOut(event);
        BorderManager.onPlayerLoggedOut(event);
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent.TeleportCommand event){
        BorderManager.onEntityTeleport(event);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event){
        RpgPlayerPropertyManager.onLivingDrops(event);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event){
        RpgPlayerPropertyManager.onLivingDeath(event);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        RpgPlayerPropertyManager.onPlayerRespawn(event);
    }
}
