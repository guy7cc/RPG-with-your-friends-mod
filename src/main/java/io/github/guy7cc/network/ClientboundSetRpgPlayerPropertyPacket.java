package io.github.guy7cc.network;

import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.sync.BorderManager;
import io.github.guy7cc.sync.RpgPlayerPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundSetRpgPlayerPropertyPacket {
    private UUID playerUUID;
    private RpgPlayerProperty property;

    public ClientboundSetRpgPlayerPropertyPacket(ServerPlayer player, RpgPlayerProperty property){
        this.playerUUID = player.getUUID();
        this.property = property;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeUUID(playerUUID);
        property.writeToBuf(buf);
    }

    public ClientboundSetRpgPlayerPropertyPacket(FriendlyByteBuf buf){
        playerUUID = buf.readUUID();
        property = new RpgPlayerProperty(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                RpgPlayerPropertyManager.update(playerUUID, property);
                ctx.get().setPacketHandled(true);
            });
        });
    }
}
