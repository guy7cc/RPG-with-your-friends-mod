package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.resource.DimensionData;
import io.github.guy7cc.resource.DimensionDataManager;
import io.github.guy7cc.resource.TraderDataManager;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.*;
import io.github.guy7cc.syncdata.BorderManager;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenOpenEvent;
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
        if(event.getObject() instanceof ServerPlayer player){
            if(!event.getObject().getCapability(PlayerMpProvider.PLAYER_MP_CAPABILITY).isPresent()){
                event.addCapability(PlayerMpProvider.PLAYER_MP_LOCATION, new PlayerMpProvider(() -> new PlayerMp(player)));
                event.addCapability(PlayerMoneyProvider.PLAYER_MONEY_LOCATION, new PlayerMoneyProvider(() -> new PlayerMoney(player)));
                event.addCapability(PlayerMiscDataProvider.PLAYER_MISC_LOCATION, new PlayerMiscDataProvider(PlayerMiscData::new));
            }
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
        PlayerMoneyProvider.onPlayerCloned(event);
        PlayerMiscDataProvider.onPlayerCloned(event);
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
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

        //party list
        PartyList.init(((ServerLevel) level).getServer());

        //mp
        PlayerMpManager.syncToClient(player);

        //money
        PlayerMoneyManager.syncToClient(player);

        //keepInventory
        KeepInventoryManager.addOrModifyPlayer(player, true);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        //party list
        if(PartyList.initedOnce()) PartyList.getInstance().forceLeaveParty(player.getUUID());

        //keepInventory
        KeepInventoryManager.removePlayerIfPresent(player);

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

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event){
        event.addListener(DimensionDataManager.instance);
        event.addListener(TraderDataManager.instance);
    }
}
