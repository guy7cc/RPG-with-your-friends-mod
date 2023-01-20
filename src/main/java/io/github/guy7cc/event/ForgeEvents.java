package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.resource.*;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.*;
import io.github.guy7cc.sync.BorderManager;
import io.github.guy7cc.sync.RpgPlayerPropertyManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof ServerPlayer){
            event.addCapability(RpgPlayerPropertyProvider.RPG_PLAYER_PROPERTY_LOCATION, new RpgPlayerPropertyProvider(RpgPlayerProperty::new));
        }
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            BorderManager.clearList(player);
        }
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
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if(!event.world.isClientSide){
            ServerLevel level = (ServerLevel) event.world;
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        ServerLevel level = (ServerLevel) event.getPlayer().level;
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        RpgPlayerPropertyManager.onPlayerLoggedIn(event);

        //party list
        PartyList.init(((ServerLevel) level).getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        RpgPlayerPropertyManager.onPlayerLoggedOut(event);

        //party list
        if(PartyList.initedOnce()) PartyList.getInstance().leaveParty(player.getUUID());

        //border
        BorderManager.clearList(player);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        JoinRequestCommand.register(dispatcher);
        RpgwDebugCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent.TeleportCommand event){
        if(event.getEntity() instanceof Player){
            ServerPlayer player = (ServerPlayer) event.getEntity();
            BorderManager.removeIfOutside(player, event.getTarget());
            BorderManager.onChange(player);
        }

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

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event){
        event.addListener(DimensionDataManager.instance);
        event.addListener(RpgStageManager.instance);
        event.addListener(RpgScenarioManager.instance);
        event.addListener(TraderDataManager.instance);
    }
}
