package io.github.guy7cc.network;

import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.resource.RpgStage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundEditDataPacket {
    private BlockPos pos;
    private String data;

    public ServerboundEditDataPacket(BlockPos pos, String data){
        this.pos = pos;
        this.data = data;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(pos);
        buf.writeUtf(data);
    }

    public ServerboundEditDataPacket(FriendlyByteBuf buf){
        pos = buf.readBlockPos();
        data = buf.readUtf();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player != null){
                ServerLevel level = player.getLevel();
                BlockState state = level.getBlockState(pos);
                BlockEntity be = level.getBlockEntity(pos);
                boolean changed = false;
                if(be instanceof VendingMachineBlockEntity){
                    ((VendingMachineBlockEntity) be).setDefaultData(new ResourceLocation(data));
                    changed = true;
                } else if(be instanceof RpgStageBlockEntity){
                    ((RpgStageBlockEntity) be).setStage(new ResourceLocation(data));
                    changed = true;
                }
                if(changed){
                    be.setChanged();
                    level.sendBlockUpdated(pos, state, state, 3);
                    ctx.get().setPacketHandled(true);
                }
            }
        });
    }
}
