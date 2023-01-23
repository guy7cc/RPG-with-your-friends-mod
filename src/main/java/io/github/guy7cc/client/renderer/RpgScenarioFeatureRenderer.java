package io.github.guy7cc.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.RpgScenarioFeature;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class RpgScenarioFeatureRenderer {
    protected static ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/rpg_scenario_feature_icon.png");

    public static void render(RpgScenarioFeature feature, PoseStack poseStack, int x, int y){
        if(feature instanceof RpgScenarioFeature.Adventure){
            render((RpgScenarioFeature.Adventure) feature, poseStack, x, y);
        } else if(feature instanceof RpgScenarioFeature.KeepInventory){
            render((RpgScenarioFeature.KeepInventory) feature, poseStack, x, y);
        }
    }

    public static void render(RpgScenarioFeature.Adventure feature, PoseStack poseStack, int x, int y){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, LOCATION);
        GuiComponent.blit(poseStack, x, y, 10, 10, feature.isAdventure() ? 0 : 10, 0, 10, 10, 32, 32);
    }

    public static void render(RpgScenarioFeature.KeepInventory feature, PoseStack poseStack, int x, int y){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, LOCATION);
        GuiComponent.blit(poseStack, x, y, 10, 10, feature.isInventoryKept() ? 0 : 10, 10, 10, 10, 32, 32);
    }
}
