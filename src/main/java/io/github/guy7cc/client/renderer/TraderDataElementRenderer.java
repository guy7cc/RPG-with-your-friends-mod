package io.github.guy7cc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.TraderDataElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.time.Instant;

public class TraderDataElementRenderer {
    public static final ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/trader.png");
    public static final int WIDTH = 114;
    public static final int HEIGHT = 22;

    private static void innerRender(TraderDataElement element, ItemRenderer itemRenderer, PoseStack poseStack, int x, int y, Minecraft minecraft){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, LOCATION);
        GuiComponent.blit(poseStack, x, y, 0, 206, WIDTH, HEIGHT, 256, 256);
        itemRenderer.renderAndDecorateItem(element.getItemStack(), x + 3, y + 3);
        itemRenderer.renderGuiItemDecorations(minecraft.font, element.getItemStack(), x + 3, y + 3, null);
    }

    public static void render(TraderDataElement element, ItemRenderer itemRenderer, PoseStack poseStack, int x, int y){
        if(element instanceof TraderDataElement.Buy){
            render((TraderDataElement.Buy) element, itemRenderer, poseStack, x, y);
        } else if(element instanceof TraderDataElement.Sell){
            render((TraderDataElement.Sell) element, itemRenderer, poseStack, x, y);
        } else if(element instanceof TraderDataElement.Barter){
            render((TraderDataElement.Barter) element, itemRenderer, poseStack, x, y);
        }
    }

    public static void render(TraderDataElement.Buy buy, ItemRenderer itemRenderer, PoseStack poseStack, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        innerRender(buy, itemRenderer, poseStack, x, y, minecraft);
        TextComponent component = new TextComponent(buy.getPrice() + " " + RpgwMod.CURRENCY);
        minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0x404040);
    }

    public static void render(TraderDataElement.Sell sell, ItemRenderer itemRenderer, PoseStack poseStack, int x, int y){
        Minecraft minecraft = Minecraft.getInstance();
        innerRender(sell, itemRenderer, poseStack, x, y, minecraft);
        if(Instant.now().toEpochMilli() > sell.getAvailableFrom()){
            Component component = new TextComponent(sell.getPrice() + " " + RpgwMod.CURRENCY);
            minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0x404040);
            String cnt = String.valueOf(sell.getCount());
            if(sell.getCount() <= 3){
                cnt = "§4" + cnt + "§8";
            }
            component = new TranslatableComponent("gui.rpgwmod.trader.upto", cnt);
            minecraft.font.draw(poseStack, component, x + 23, y + 7.5f, 0x555555);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, LOCATION);
            GuiComponent.blit(poseStack, x, y, 114, 206, WIDTH, HEIGHT, 256, 256);
            itemRenderer.renderAndDecorateItem(sell.getItemStack(), x + 3, y + 3);
            itemRenderer.renderGuiItemDecorations(minecraft.font, sell.getItemStack(), x + 3, y + 3, null);

            long now = Instant.now().toEpochMilli();
            long s = (sell.getAvailableFrom() - now) / 1000;
            long m = s / 60;
            long h = m / 60;

            Component component = new TextComponent(sell.getPrice() + " " + RpgwMod.CURRENCY);
            minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0xbfbfbf);
            String cnt = String.valueOf(sell.getCount());
            if(sell.getCount() <= 3){
                cnt = "§4" + cnt + "§8";
            }
            component = new TranslatableComponent("gui.rpgwmod.trader.available", h > 0 ? h + "h" : m > 0 ? m + "m" : s + "s");
            minecraft.font.draw(poseStack, component, x + 23, y + 7.5f, 0xdddddd);
        }
    }

    public static void render(TraderDataElement.Barter barter, ItemRenderer itemRenderer, PoseStack poseStack, int x, int y){
        Minecraft minecraft = Minecraft.getInstance();
        innerRender(barter, itemRenderer, poseStack, x, y, minecraft);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, LOCATION);
        poseStack.pushPose();
        poseStack.translate(0, 0, -100);
        GuiComponent.blit(poseStack, x + 94, y + 2, 2, 208, 18, 18, 256, 256);
        poseStack.popPose();
        GuiComponent.blit(poseStack, x + 45, y + 3, 0, 228, 24, 16, 256, 256);
        itemRenderer.renderAndDecorateItem(barter.getRequirement(), x + 95, y + 3);
        itemRenderer.renderGuiItemDecorations(minecraft.font, barter.getRequirement(), x + 95, y + 3, null);
    }
}
