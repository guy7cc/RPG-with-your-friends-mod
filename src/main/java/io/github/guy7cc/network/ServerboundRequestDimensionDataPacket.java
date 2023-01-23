package io.github.guy7cc.network;

import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.resource.DimensionDataManager;
import io.github.guy7cc.rpg.Border;
import io.github.guy7cc.rpg.BorderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ServerboundRequestDimensionDataPacket {
    public ServerboundRequestDimensionDataPacket(){ }

    public void toBytes(FriendlyByteBuf buf){ }

    public ServerboundRequestDimensionDataPacket(FriendlyByteBuf buf){ }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            ResourceLocation dimLoc = player.getLevel().dimension().location();
            if(DimensionDataManager.instance.containsKey(dimLoc))
                RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSetDimensionDataPacket(dimLoc, DimensionDataManager.instance.getOrDefault(dimLoc)));
            ctx.get().setPacketHandled(true);
        });
    }
}
