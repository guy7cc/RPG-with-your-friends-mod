package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        Level level = event.getPlayer().level;
        if(!level.isClientSide){
            PartyList.init(((ServerLevel) level).getServer());
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
    }
}
