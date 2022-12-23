package io.github.guy7cc.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.save.cap.PlayerMp;
import io.github.guy7cc.syncdata.PlayerMpManager;
import io.github.guy7cc.util.BlockStateJsonConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class RpgwDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rpgwdebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("mp")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(ctx ->
                                        mp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value")
                                        )
                                )
                        )
                )
                .then(Commands.literal("setBlockEntity")
                        .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                .executes(ctx -> {
                                    return setBlockEntity(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "blockpos"));
                                })
                        )
                )
                .then(Commands.literal("getBlockEntity")
                        .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                .executes(ctx -> {
                                    return getBlockEntity(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "blockpos"));
                                })
                        )
                )
                .then(Commands.literal("saveBlockState")
                        .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            return saveBlockState(
                                                    ctx.getSource(),
                                                    BlockPosArgument.getLoadedBlockPos(ctx, "blockpos"),
                                                    StringArgumentType.getString(ctx, "name")
                                            );
                                        })
                                )
                        )
                )
                .then(Commands.literal("setBlock")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ctx -> {
                                    return setBlock(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                })
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

    private static int setBlockEntity(CommandSourceStack source, BlockPos pos) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.getLevel();
        level.setBlockEntity(new VendingMachineBlockEntity(pos, level.getBlockState(pos)));
        return 1;
    }

    private static int getBlockEntity(CommandSourceStack source, BlockPos pos) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.getLevel();
        BlockEntity be = level.getBlockEntity(pos);
        if(be == null) player.displayClientMessage(new TextComponent("null"), false);
        else player.displayClientMessage(new TextComponent(be.toString()), false);
        return 1;
    }

    private static int saveBlockState(CommandSourceStack source, BlockPos pos, String name) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        JsonObject obj = BlockStateJsonConverter.toJson(level, pos);
        Path folderPath = Path.of(level.getStructureManager().generatedDir + "\\" + RpgwMod.MOD_ID + "\\blockstates");
        Path filePath = Path.of(folderPath + "\\" + name + ".json");
        try{
            if(!Files.exists(folderPath)) Files.createDirectories(folderPath);
            if(!Files.exists(filePath)) Files.createFile(filePath);
            FileWriter fw = new FileWriter(filePath.toString(), false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            pw.print(obj.toString());
            pw.close();
        } catch(IOException e){
            e.printStackTrace();
            return 0;
        }
        source.sendSuccess(new TextComponent("Successfully saved the block state as a json file: " + filePath), true);
        return 1;
    }

    private static int setBlock(CommandSourceStack source, String fileName){
        ServerLevel level = source.getLevel();
        Path filePath = Path.of(level.getStructureManager().generatedDir + "\\" + RpgwMod.MOD_ID + "\\blockstates\\" + fileName + ".json");
        try{
            if(!Files.exists(filePath)) return 0;
            String text = Files.readString(filePath);
            JsonObject obj = GsonHelper.parse(text);
            BlockStateJsonConverter.setBlockFromJson(obj, level);
        } catch(IOException e){
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
