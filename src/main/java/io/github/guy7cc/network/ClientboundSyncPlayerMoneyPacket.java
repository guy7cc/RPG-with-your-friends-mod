package io.github.guy7cc.network;

import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncPlayerMoneyPacket {
    private long money;

    public ClientboundSyncPlayerMoneyPacket(long money){
        this.money = money;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeLong(money);
    }

    public ClientboundSyncPlayerMoneyPacket(FriendlyByteBuf buf){
        this.money = buf.readLong();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                PlayerMoneyManager.setPlayerMoney(this.money);
                ctx.get().setPacketHandled(true);
            });
        });
    }
}
