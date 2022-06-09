package io.github.guy7cc.client.event;

import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {
    public static final ResourceLocation SOCIAL_MENU_BUTTON = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/social_menu_button.png");
    @SubscribeEvent
    public static void onInitScreenPre(ScreenEvent.InitScreenEvent.Post event){
        if(event.getScreen() instanceof InventoryScreen screen){
            event.addListener(new ImageButton(screen.getGuiLeft() + screen.getXSize() + 1, screen.getGuiTop() + screen.getYSize() - 20, 20, 20, 0, 0, 20, SOCIAL_MENU_BUTTON, 32, 64, button -> {
                Minecraft.getInstance().player.displayClientMessage(new TextComponent("hit the button!"), false);
            }));
        }
    }

}
