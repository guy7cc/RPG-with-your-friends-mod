package io.github.guy7cc.network;

import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClientboundSyncPartyPacket {
    public CompoundTag partyTag;

    public ClientboundSyncPartyPacket(@Nullable Party party){
        partyTag = party != null ? party.serializeNBT() : null;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeNbt(partyTag);
    }

    public ClientboundSyncPartyPacket(FriendlyByteBuf buf){
        this.partyTag = buf.readNbt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                PartyManager.refresh(partyTag);
                ctx.get().setPacketHandled(true);
            });
        });
    }
}
