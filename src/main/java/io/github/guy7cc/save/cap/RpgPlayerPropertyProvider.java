package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RpgPlayerPropertyProvider  extends AbstractCapabilityProvider<RpgPlayerProperty> {
    public static final ResourceLocation RPG_PLAYER_PROPERTY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "rpg_player_property");
    public static final Capability<RpgPlayerProperty> RPG_PLAYER_PROPERTY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public RpgPlayerPropertyProvider(Supplier<RpgPlayerProperty> defaultSupplier){
        super(defaultSupplier);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == RPG_PLAYER_PROPERTY_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof ServerPlayer player){
            event.addCapability(RPG_PLAYER_PROPERTY_LOCATION, new RpgPlayerPropertyProvider(RpgPlayerProperty::new));
        }
    }
}
