package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlayerMpProvider extends AbstractCapabilityProvider<PlayerMp> {
    public static final ResourceLocation PLAYER_MP_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "player_mp");
    public static final Capability<PlayerMp> PLAYER_MP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public PlayerMpProvider(Supplier<PlayerMp> defaultSupplier){
        super(defaultSupplier);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == PLAYER_MP_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }
}