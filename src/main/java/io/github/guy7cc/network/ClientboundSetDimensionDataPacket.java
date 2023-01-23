package io.github.guy7cc.network;

import io.github.guy7cc.client.renderer.DimensionDataRenderer;
import io.github.guy7cc.resource.DimensionData;
import io.github.guy7cc.resource.DimensionDataManager;
import io.github.guy7cc.resource.RpgScenario;
import io.github.guy7cc.resource.RpgScenarioManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSetDimensionDataPacket {
    private ResourceLocation loc;
    private DimensionData data;

    public ClientboundSetDimensionDataPacket(ResourceLocation loc, DimensionData data){
        this.loc = loc;
        this.data = data;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeResourceLocation(loc);
        buf.writeNbt((CompoundTag)DimensionData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElse(null));
    }

    public ClientboundSetDimensionDataPacket(FriendlyByteBuf buf){
        loc = buf.readResourceLocation();
        CompoundTag tag = buf.readNbt();
        data = DimensionData.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if(data != null){
                    DimensionDataRenderer.show(data);
                    ctx.get().setPacketHandled(true);
                }
            });
        });
    }
}
