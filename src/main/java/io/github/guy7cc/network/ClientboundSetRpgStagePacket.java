package io.github.guy7cc.network;

import io.github.guy7cc.client.screen.RpgStageScreen;
import io.github.guy7cc.resource.RpgStage;
import io.github.guy7cc.resource.RpgStageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSetRpgStagePacket {
    private ResourceLocation loc;
    private RpgStage stage;

    public ClientboundSetRpgStagePacket(ResourceLocation loc, RpgStage stage){
        this.loc = loc;
        this.stage = stage;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeResourceLocation(loc);
        buf.writeNbt((CompoundTag)RpgStage.CODEC.encodeStart(NbtOps.INSTANCE, stage).result().orElse(null));
    }

    public ClientboundSetRpgStagePacket(FriendlyByteBuf buf){
        loc = buf.readResourceLocation();
        CompoundTag tag = buf.readNbt();
        stage = RpgStage.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if(stage != null){
                    RpgStageManager.instance.putToClient(loc, stage);
                    if(Minecraft.getInstance().screen instanceof RpgStageScreen screen){
                        screen.handlePacket(this);
                    }
                    ctx.get().setPacketHandled(true);
                }
            });
        });
    }

    public ResourceLocation getLocation(){
        return loc;
    }
}
