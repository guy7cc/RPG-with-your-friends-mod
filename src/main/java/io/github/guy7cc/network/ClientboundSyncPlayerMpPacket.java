package io.github.guy7cc.network;

import io.github.guy7cc.sync.PlayerMpManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundSyncPlayerMpPacket {
    private float playerMp;
    private float playerMaxMp;
    private UUID uuid;

    public ClientboundSyncPlayerMpPacket(float playerMp, float playerMaxMp, UUID uuid){
        this.playerMp = playerMp;
        this.playerMaxMp = playerMaxMp;
        this.uuid = uuid;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeFloat(playerMp);
        buf.writeFloat(playerMaxMp);
        buf.writeUUID(uuid);
    }

    public ClientboundSyncPlayerMpPacket(FriendlyByteBuf buf){
        this.playerMp = buf.readFloat();
        this.playerMaxMp = buf.readFloat();
        this.uuid = buf.readUUID();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                PlayerMpManager.setPlayerMp(this.uuid, this.playerMp);
                PlayerMpManager.setPlayerMaxMp(this.uuid, this.playerMp);
                ctx.get().setPacketHandled(true);
            });
        });
    }
}
