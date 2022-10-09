package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MiscPlayerDataProvider extends AbstractCapabilityProvider<MiscPlayerData> {
    public static final ResourceLocation PLAYER_MISC_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "keep_inventory");
    public static final Capability<MiscPlayerData> PLAYER_MISC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public MiscPlayerDataProvider(Supplier<MiscPlayerData> defaultSupplier) {
        super(defaultSupplier);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == PLAYER_MISC_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }
}
