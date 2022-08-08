package io.github.guy7cc.client.event;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.block.entity.RpgwBlockEntities;
import io.github.guy7cc.block.entity.renderer.RpgwSpawnerBlockEntityRenderer;
import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void onFMLClientSetUp(FMLClientSetupEvent event){
        ItemBlockRenderTypes.setRenderLayer(RpgwBlocks.RPGW_SPAWNER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(RpgwBlocks.BORDERED_RPGW_SPAWNER.get(), RenderType.translucent());

        OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, false);
        OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
        RpgwIngameOverlay.registerOverlay();
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(RpgwBlockEntities.RPGW_SPAWNER.get(), ctx -> new RpgwSpawnerBlockEntityRenderer());
        event.registerBlockEntityRenderer(RpgwBlockEntities.BORDERED_RPGW_SPAWNER.get(), ctx -> new RpgwSpawnerBlockEntityRenderer());
    }
}
