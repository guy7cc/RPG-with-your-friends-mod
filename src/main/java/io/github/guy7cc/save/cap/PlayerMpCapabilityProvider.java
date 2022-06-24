package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMpCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation PLAYER_MP_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "player_mp");
    public static final Capability<PlayerMp> PLAYER_MP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMp handler = null;
    private final LazyOptional<PlayerMp> holder = LazyOptional.of(this::getHandler);

    private PlayerMp getHandler(){
        if(handler == null){
            handler = new PlayerMp(20);
        }
        return handler;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap){
        if(cap == PLAYER_MP_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        return getHandler().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getHandler().deserializeNBT(nbt);
    }
}
