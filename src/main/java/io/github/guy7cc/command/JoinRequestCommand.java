package io.github.guy7cc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class JoinRequestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("jreq")
                .then(Commands.literal("accept")
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                        .executes(ctx ->
                                                accept(ctx.getSource(), UuidArgument.getUuid(ctx, "uuid"), IntegerArgumentType.getInteger(ctx, "id"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("deny")
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                .then(Commands.argument("id", IntegerArgumentType.integer())
                                        .executes(ctx ->
                                                deny(ctx.getSource(), UuidArgument.getUuid(ctx, "uuid"), IntegerArgumentType.getInteger(ctx, "id"))
                                        )
                                )
                        )
                )
        );
    }
    private static int accept(CommandSourceStack source, UUID uuid, int id) throws CommandSyntaxException {
        PartyList partyList = PartyList.getInstance();
        ServerPlayer leader = source.getPlayerOrException();
        ServerPlayer member = partyList.getServer().getPlayerList().getPlayer(uuid);
        Party partyById = partyList.byId(id);
        Party partyByLeader = partyList.byLeader(leader.getUUID());
        if(partyById != null && partyByLeader != null && member != null && partyById == partyByLeader && !partyList.inParty(uuid)){
            partyList.joinParty(uuid, id);
            leader.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqAccepted"), false);
            member.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqAccepted"), false);
        }
        else{
            RpgwMod.LOGGER.info(partyById.serializeNBT().toString());
            RpgwMod.LOGGER.info(partyByLeader.serializeNBT().toString());
            RpgwMod.LOGGER.info(leader.getName().getString());
            RpgwMod.LOGGER.info(member.getName().getString());

            leader.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqFail"), false);
        }
        return 0;
    }
    private static int deny(CommandSourceStack source, UUID uuid, int id) throws CommandSyntaxException{
        PartyList partyList = PartyList.getInstance();
        ServerPlayer leader = source.getPlayerOrException();
        ServerPlayer member = partyList.getServer().getPlayerList().getPlayer(uuid);
        Party partyById = partyList.byId(id);
        Party partyByLeader = partyList.byLeader(leader.getUUID());
        if(partyById != null && partyByLeader != null && member != null && partyById == partyByLeader && !partyList.inParty(uuid)){
            leader.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqDenied"), false);
            member.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqDenied"), false);
        }
        else{
            RpgwMod.LOGGER.info(partyById.serializeNBT().toString());
            RpgwMod.LOGGER.info(partyByLeader.serializeNBT().toString());
            RpgwMod.LOGGER.info(leader.getName().getString());
            RpgwMod.LOGGER.info(member.getName().getString());
            leader.displayClientMessage(new TranslatableComponent("commands.rpgw.jreqFail"), false);
        }
        return 0;
    }

    private static boolean isPlayer(UUID uuid, MinecraftServer server){
        return server.getPlayerList().getPlayer(uuid) != null;
    }
}
