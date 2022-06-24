package io.github.guy7cc.network;

import io.github.guy7cc.client.screen.party.PartyMenuScreen;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundManagePartyPacket {
    public CompoundTag partyListTag;

    public ClientboundManagePartyPacket(PartyList list){
        partyListTag = list.serializeNBT();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeNbt(partyListTag);
    }

    public ClientboundManagePartyPacket(FriendlyByteBuf buf){
        this.partyListTag = buf.readNbt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if(Minecraft.getInstance().screen instanceof PartyMenuScreen partyMenuScreen){
                    partyMenuScreen.refresh(partyListTag);
                }
                ctx.get().setPacketHandled(true);
            });
        });
    }



}
