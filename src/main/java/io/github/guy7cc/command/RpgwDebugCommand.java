package io.github.guy7cc.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.save.cap.PropertyType;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.sync.RpgPlayerPropertyManager;
import io.github.guy7cc.util.BlockStateJsonConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class RpgwDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rpgwdebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setPlayerProperty")
                        .then(Commands.literal("mp")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(ctx -> setPlayerProperty(ctx.getSource(), PropertyType.MP, FloatArgumentType.getFloat(ctx, "value")))))
                        .then(Commands.literal("maxMp")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(ctx -> setPlayerProperty(ctx.getSource(), PropertyType.MAX_MP, FloatArgumentType.getFloat(ctx, "value")))))
                        .then(Commands.literal("money")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(ctx -> setPlayerProperty(ctx.getSource(), PropertyType.MONEY, LongArgumentType.getLong(ctx, "value")))))
                        .then(Commands.literal("maxMoney")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(ctx -> setPlayerProperty(ctx.getSource(), PropertyType.MAX_MONEY, LongArgumentType.getLong(ctx, "value")))))
                        .then(Commands.literal("isInventoryKept")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            RpgPlayerProperty property = RpgPlayerPropertyManager.get(player);
                                            if(property == null) return 0;
                                            property.isInventoryKept = BoolArgumentType.getBool(ctx, "value");
                                            return 1;
                                        }))))
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

    private static <T> int setPlayerProperty(CommandSourceStack source, PropertyType<T> type, T value) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        RpgPlayerProperty property = RpgPlayerPropertyManager.get(player);
        if(property == null) return 0;
        property.setValue(type, value);
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
