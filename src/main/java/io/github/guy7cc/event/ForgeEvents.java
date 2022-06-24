package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.command.RpgwDebugCommand;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.PlayerMpCapabilityProvider;
import io.github.guy7cc.syncdata.DataSyncManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            if(!event.getObject().getCapability(PlayerMpCapabilityProvider.PLAYER_MP_CAPABILITY).isPresent()){
                event.addCapability(PlayerMpCapabilityProvider.PLAYER_MP_LOCATION, new PlayerMpCapabilityProvider());
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
        Level level = event.getPlayer().level;
        if(!level.isClientSide){
            PartyList.init(((ServerLevel) level).getServer());
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            DataSyncManager.syncLogIn(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getPlayer();
        if(!player.level.isClientSide){
            PartyList.getInstance().forceLeaveParty(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        JoinRequestCommand.register(dispatcher);
        RpgwDebugCommand.register(dispatcher);
    }
}
