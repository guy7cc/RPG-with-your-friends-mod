package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMiscCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation PLAYER_MISC_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "keep_inventory");
    public static final Capability<PlayerMiscCap> PLAYER_MISC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerMiscCap handler = null;
    private final LazyOptional<PlayerMiscCap> holder = LazyOptional.of(this::getHandler);

    private PlayerMiscCap getHandler(){
        if(handler == null){
            handler = new PlayerMiscCap();
        }
        return handler;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap){
        if(cap == PLAYER_MISC_CAPABILITY) {
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
