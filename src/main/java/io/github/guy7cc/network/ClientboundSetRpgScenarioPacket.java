package io.github.guy7cc.network;

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

public class ClientboundSetRpgScenarioPacket {
    private ResourceLocation loc;
    private RpgScenario scenario;

    public ClientboundSetRpgScenarioPacket(ResourceLocation loc, RpgScenario scenario){
        this.loc = loc;
        this.scenario = scenario;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeResourceLocation(loc);
        buf.writeNbt((CompoundTag)RpgScenario.CODEC.encodeStart(NbtOps.INSTANCE, scenario).result().orElse(null));
    }

    public ClientboundSetRpgScenarioPacket(FriendlyByteBuf buf){
        loc = buf.readResourceLocation();
        CompoundTag tag = buf.readNbt();
        scenario = RpgScenario.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if(scenario != null){
                    RpgScenarioManager.instance.putToClient(loc, scenario);
                    ctx.get().setPacketHandled(true);
                }
            });
        });
    }
}
