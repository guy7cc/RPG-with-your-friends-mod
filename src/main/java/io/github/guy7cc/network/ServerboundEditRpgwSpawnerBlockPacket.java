package io.github.guy7cc.network;

import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundEditRpgwSpawnerBlockPacket {
    private BlockPos pos;
    private CompoundTag tag;

    public ServerboundEditRpgwSpawnerBlockPacket(BlockPos pos, CompoundTag tag){
        this.pos = pos;
        this.tag = tag;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(pos);
        buf.writeNbt(tag);
    }

    public ServerboundEditRpgwSpawnerBlockPacket(FriendlyByteBuf buf){
        this.pos = buf.readBlockPos();
        this.tag = buf.readNbt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player != null){
                ServerLevel level = (ServerLevel) player.level;
                BlockEntity blockEntity = level.getBlockEntity(this.pos);
                if(blockEntity instanceof RpgwSpawnerBlockEntity){
                    blockEntity.load(this.tag);
                    blockEntity.setChanged();
                    level.sendBlockUpdated(this.pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                    ctx.get().setPacketHandled(true);
                }
            }
        });
    }
}
