package io.github.guy7cc.client;

import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.client.screen.RpgStageScreen;
import io.github.guy7cc.client.screen.RpgwEditDataScreen;
import io.github.guy7cc.client.screen.TraderScreen;
import io.github.guy7cc.item.RpgwItems;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundRequestDimensionDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenOpenEvent;

public class ClientExecutionUtil {
    public static Runnable openScreenForVendingMachineBlock(VendingMachineBlockEntity vm, BlockPos pos, Player player, InteractionHand hand){
        return () -> {
            if(player.getItemInHand(hand).is(RpgwItems.DEBUG_WRENCH.get())){
                Minecraft.getInstance().setScreen(new RpgwEditDataScreen(pos, vm.getDefaultData().toString()));
            } else {
                Minecraft.getInstance().setScreen(new TraderScreen(vm));
            }
        };
    }

    public static Runnable openScreenForRpgStageBlock(RpgStageBlockEntity be, BlockPos pos, Player player, InteractionHand hand){
        return () -> {
            if(player.getItemInHand(hand).is(RpgwItems.DEBUG_WRENCH.get())){
                Minecraft.getInstance().setScreen(new RpgwEditDataScreen(pos, be.getStage().toString()));
            } else {
                Minecraft.getInstance().setScreen(new RpgStageScreen(be));
            }
        };
    }

    public static Runnable dimensionDataOnScreenOpen(ScreenOpenEvent event){
        return () -> {
            Minecraft minecraft = Minecraft.getInstance();
            if(minecraft.screen instanceof ReceivingLevelScreen){
                RpgwMessageManager.sendToServer(new ServerboundRequestDimensionDataPacket());
            }
        };
    }
}
