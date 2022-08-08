package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.PlayerMiscCapabilityProvider;
import io.github.guy7cc.save.cap.PlayerMpCapabilityProvider;
import io.github.guy7cc.syncdata.BorderManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import io.github.guy7cc.save.cap.KeepInventoryManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
            if(!event.getObject().getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY).isPresent()){
                event.addCapability(PlayerMpCapabilityProvider.PLAYER_MP_LOCATION, new PlayerMpCapabilityProvider(player));
                event.addCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_LOCATION, new PlayerMiscCapabilityProvider());
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
        event.getOriginal().getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).ifPresent(oldCap -> {
            event.getPlayer().getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).ifPresent(newCap -> {
                newCap.keepInventory = oldCap.keepInventory;
            });
        });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            Player player = event.player;
            if(player.level.isClientSide) {
                Border border = BorderManager.clientBorder;
                if(border != null){
                    Vec3 pos = player.position();
                    Vec3 delta = player.getDeltaMovement();
                    double x = pos.x;
                    double deltaX = delta.x;
                    double z = pos.z;
                    double deltaZ = delta.z;
                    if(x < border.minX + 0.3D){
                        x = border.minX + 0.3D;
                        deltaX = 0;
                    } else if(x > border.maxX - 0.3D){
                        x = border.maxX - 0.3D;
                        deltaX = 0;
                    }
                    if(z < border.minZ + 0.3D){
                        z = border.minZ + 0.3D;
                        deltaZ = 0;
                    } else if(z > border.maxZ - 0.3D){
                        z = border.maxZ - 0.3D;
                        deltaZ = 0;
                    }
                    player.setPos(x, pos.y, z);
                    player.setDeltaMovement(deltaX, delta.y, deltaZ);
                }
            } else {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                Border border = BorderManager.getCurrentBorder(serverPlayer);
                if(border != null && border.outsideEnough(serverPlayer.position())){
                    double x = serverPlayer.getX();
                    double z = serverPlayer.getZ();
                    if(x <= border.minX - 1) x = border.minX + 0.3D;
                    else if(x >= border.maxX + 1) x = border.maxX - 0.3D;
                    if(z <= border.minZ - 1) z = border.minZ + 0.3D;
                    else if(z >= border.maxZ + 1) z = border.maxZ - 0.3D;
                    serverPlayer.teleportTo(x, serverPlayer.getY(), z);
                }

            }
        }
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

        //mp
        PlayerMpManager.syncMpToClient(player);
        PlayerMpManager.syncMaxMpToClient(player);

        //keepInventory
        KeepInventoryManager.addOrModifyPlayer(player, false);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        //party list
        PartyList.getInstance().forceLeaveParty(player.getUUID());

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
}
