package io.github.guy7cc.network;

import io.github.guy7cc.block.VendingMachineBlock;
import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.client.screen.TraderScreen;
import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.syncdata.BorderManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncVendingMachinePacket {
    public BlockPos blockPos;
    public TraderData data;

    public ClientboundSyncVendingMachinePacket(VendingMachineBlockEntity be){
        blockPos = be.getBlockPos();
        data = be.getTraderData();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(blockPos);
        buf.writeNbt(data.serializeNBT());
    }

    public ClientboundSyncVendingMachinePacket(FriendlyByteBuf buf){
        blockPos = buf.readBlockPos();
        data = new TraderData(buf.readNbt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft minecraft = Minecraft.getInstance();
                if(minecraft.level.getBlockEntity(blockPos) instanceof VendingMachineBlockEntity be){
                    be.setTraderData(data);

                    if(minecraft.screen instanceof TraderScreen screen){
                        screen.updateGui(data);
                    }

                    ctx.get().setPacketHandled(true);
                }
            });
        });
    }
}
