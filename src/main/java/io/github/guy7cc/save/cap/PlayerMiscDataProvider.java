package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlayerMiscDataProvider extends AbstractCapabilityProvider<PlayerMiscData> {
    public static final ResourceLocation PLAYER_MISC_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "keep_inventory");
    public static final Capability<PlayerMiscData> PLAYER_MISC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public PlayerMiscDataProvider(Supplier<PlayerMiscData> defaultSupplier) {
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

    public static void onPlayerCloned(PlayerEvent.Clone event){
        event.getOriginal().getCapability(PlayerMiscDataProvider.PLAYER_MISC_CAPABILITY).ifPresent(oldCap -> {
            event.getPlayer().getCapability(PlayerMiscDataProvider.PLAYER_MISC_CAPABILITY).ifPresent(newCap -> {
                newCap.keepInventory = oldCap.keepInventory;
            });
        });
    }
}
