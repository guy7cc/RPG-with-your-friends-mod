package io.github.guy7cc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.guy7cc.save.cap.PlayerMp;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class RpgwDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rpgwdebug")
                .then(Commands.literal("mp")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(ctx ->
                                        mp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value")
                                        )
                                )
                        )
                )
        );
    }

    private static int mp(CommandSourceStack source, int mp) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerMp playerMp = PlayerMpManager.getPlayerMp(player);
        if(playerMp != null){
            playerMp.setMp(mp);
        }
        return 1;
    }
}
