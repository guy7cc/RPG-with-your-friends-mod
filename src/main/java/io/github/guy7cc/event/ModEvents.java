package io.github.guy7cc.event;

import com.mojang.brigadier.CommandDispatcher;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.command.JoinRequestCommand;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

}
