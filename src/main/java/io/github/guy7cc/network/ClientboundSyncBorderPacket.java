package io.github.guy7cc.network;

import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncBorderPacket {
    private Border border;

    public ClientboundSyncBorderPacket(Border border){
        this.border = border;
    }

    public void toBytes(FriendlyByteBuf buf){
        if(border != null){
            buf.writeInt(border.id);
            buf.writeDouble(border.minX);
            buf.writeDouble(border.maxX);
            buf.writeDouble(border.minZ);
            buf.writeDouble(border.maxZ);
        } else {
            buf.writeInt(-1);
        }
    }

    public ClientboundSyncBorderPacket(FriendlyByteBuf buf){
        int id = buf.readInt();
        if(id != -1){
            this.border = new Border(
                    id,
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        } else {
            this.border = null;
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                BorderManager.clientBorder = this.border;
                ctx.get().setPacketHandled(true);
            });
        });
    }
}
