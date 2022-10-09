package io.github.guy7cc.event;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.save.cap.PlayerMiscData;
import io.github.guy7cc.save.cap.PlayerMp;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event){
        event.register(PlayerMp.class);
        event.register(PlayerMiscData.class);
    }
}
