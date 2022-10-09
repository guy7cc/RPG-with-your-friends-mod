package io.github.guy7cc.save.cap;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PlayerMoneyProvider extends AbstractCapabilityProvider<PlayerMoney> {
    public static final ResourceLocation PLAYER_MONEY_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "player_money");
    public static final Capability<PlayerMoney> PLAYER_MONEY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public PlayerMoneyProvider(Supplier<PlayerMoney> defaultSupplier) {
        super(defaultSupplier);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == PLAYER_MONEY_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    public static void onPlayerCloned(PlayerEvent.Clone event){
        event.getOriginal().getCapability(PlayerMoneyProvider.PLAYER_MONEY_CAPABILITY).ifPresent(oldCap -> {
            event.getPlayer().getCapability(PlayerMoneyProvider.PLAYER_MONEY_CAPABILITY).ifPresent(newCap -> {
                newCap.setMoney(oldCap.getMoney());
            });
        });
    }
}
