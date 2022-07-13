package io.github.guy7cc.network;

import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncBorderPacket {
    private Border border;
    private boolean forEdit;

    public ClientboundSyncBorderPacket(Border border, boolean forEdit){
        this.border = border;
        this.forEdit = forEdit;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBoolean(forEdit);
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
        this.forEdit = buf.readBoolean();
        int id = buf.readInt();
        //id == -1 means synced border is null
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
                if(forEdit){
                    if(Minecraft.getInstance().screen instanceof BorderBlockEditScreen screen){
                        screen.setBorder(this.border);
                        ctx.get().setPacketHandled(true);
                    }
                } else {
                    BorderManager.clientBorder = this.border;
                    ctx.get().setPacketHandled(true);
                }
            });
        });
    }
}
