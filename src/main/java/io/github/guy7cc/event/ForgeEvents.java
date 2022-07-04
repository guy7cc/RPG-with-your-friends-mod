package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.PlayerMiscCapabilityProvider;
import io.github.guy7cc.save.cap.PlayerMpCapabilityProvider;
import io.github.guy7cc.syncdata.DataSyncManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import io.github.guy7cc.save.cap.KeepInventoryManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof ServerPlayer player){
            if(!event.getObject().getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY).isPresent()){
                event.addCapability(PlayerMpCapabilityProvider.PLAYER_MP_LOCATION, new PlayerMpCapabilityProvider(player));
                event.addCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_LOCATION, new PlayerMiscCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).ifPresent(oldCap -> {
            event.getPlayer().getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).ifPresent(newCap -> {
                newCap.keepInventory = oldCap.keepInventory;
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if(!event.world.isClientSide){
            ServerLevel level = (ServerLevel) event.world;
            PlayerMpManager.serverTick(level);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        ServerLevel level = (ServerLevel) event.getPlayer().level;
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        //party list
        PartyList.init(((ServerLevel) level).getServer());

        //sync data
        DataSyncManager.syncLogIn(player);

        //keepInventory
        KeepInventoryManager.addOrModifyPlayer(player, false);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        //party list
        PartyList.getInstance().forceLeaveParty(player.getUUID());

        //sync data
        DataSyncManager.manageLogOut(player);

        //keepInventory
        KeepInventoryManager.removePlayerIfPresent(player);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        JoinRequestCommand.register(dispatcher);
        RpgwDebugCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event){
        if(event.getEntityLiving() instanceof ServerPlayer player){
            event.setCanceled(KeepInventoryManager.keepInventory(player));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event){
        if(event.getEntityLiving() instanceof ServerPlayer player && KeepInventoryManager.keepInventory(player)){
            KeepInventoryManager.collectItems(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        if(event.getPlayer() instanceof ServerPlayer player){
            KeepInventoryManager.restoreInventory(player);
        }
    }
}
