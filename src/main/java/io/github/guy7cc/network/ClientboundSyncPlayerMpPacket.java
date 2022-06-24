package io.github.guy7cc.network;

import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncPlayerMpPacket {
    private float playerMp;
    private Type type;

    public ClientboundSyncPlayerMpPacket(float playerMp, Type type){
        this.playerMp = playerMp;
        this.type = type;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeFloat(playerMp);
        buf.writeInt(type.id);
    }

    public ClientboundSyncPlayerMpPacket(FriendlyByteBuf buf){
        this.playerMp = buf.readFloat();
        this.type = Type.byId(buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if(this.type == Type.VALUE) PlayerMpManager.clientPlayerMp = this.playerMp;
                else if(this.type == Type.VALUE) PlayerMpManager.clientPlayerMpMax = this.playerMp;
                ctx.get().setPacketHandled(true);
            });
        });
    }

    public enum Type{
        VALUE(0),
        MAX(1);

        public final int id;
        Type(int id){
            this.id = id;
        }

        public static Type byId(int id){
            for(Type type : values()){
                if(type.id == id) return type;
            }
            return null;
        }
    }
}
