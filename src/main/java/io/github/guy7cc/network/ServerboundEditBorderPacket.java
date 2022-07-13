package io.github.guy7cc.network;

import io.github.guy7cc.block.AbstractBorderBlock;
import io.github.guy7cc.block.entity.AbstractBorderBlockEntity;
import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundEditBorderPacket {
    private Border border;
    private BlockPos pos;

    public ServerboundEditBorderPacket(Border border, BlockPos pos){
        this.border = border;
        this.pos = pos;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeDouble(border.minX);
        buf.writeDouble(border.maxX);
        buf.writeDouble(border.minZ);
        buf.writeDouble(border.maxZ);
        buf.writeBlockPos(pos);
    }

    public ServerboundEditBorderPacket(FriendlyByteBuf buf){
        this.border = new Border(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        );
        this.pos = buf.readBlockPos();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player != null){
                ServerLevel level = (ServerLevel) player.level;
                if(level.getBlockEntity(this.pos) instanceof AbstractBorderBlockEntity borderBE){
                    BorderManager.remove(level.getServer(), borderBE.border.id);
                    borderBE.border = this.border;
                    ctx.get().setPacketHandled(true);
                }
            }
        });
    }
}
