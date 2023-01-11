package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.resource.RpgScenario;
import io.github.guy7cc.resource.RpgScenarioManager;
import io.github.guy7cc.resource.RpgStage;
import io.github.guy7cc.resource.RpgStageManager;
import io.github.guy7cc.util.EasingFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class RpgStageScreen extends Screen {
    private static final ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/rpg_stage.png");
    private static final float textSpeed = 0.25f;

    private RpgStage stage;

    private int stageX;
    private int stageY;
    private int levelX;
    private int levelY;
    private boolean stageActive = true;

    private int activeSlot = 0;
    private RpgScenario activeScenario;
    private float cursorX;
    private float cursorY;
    private float lastCursorX;
    private float lastCursorY;

    private int tick = 0;
    private int cursorTick = 0;

    public RpgStageScreen(RpgStageBlockEntity be) {
        super(TextComponent.EMPTY);
        stage = RpgStageManager.instance.get(be.getStage());
        if(stage == null) stage = RpgStage.DEFAULT;
        setActiveScenario(0);
    }

    @Override
    protected void init() {
        stageX = width / 2 - 104;
        stageY = height / 2 - 78;
        levelX = width / 2 - 72;
        levelY = height / 2 - 33;
        cursorX = stageX + activeSlot % 9 * 18 + 4;
        cursorY = stageY + (activeSlot / 9) * 18 + 18;
        lastCursorX = cursorX;
        lastCursorY = cursorY;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderBackground(pPoseStack);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, LOCATION);

        if(stageActive){
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
            renderLevel(pPoseStack, pMouseX, pMouseY, pPartialTick);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1.0F);
            renderStage(pPoseStack, pMouseX, pMouseY, pPartialTick);
        } else {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
            renderStage(pPoseStack, pMouseX, pMouseY, pPartialTick);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1.0F);
            renderLevel(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    private void renderStage(PoseStack poseStack, int mouseX, int mouseY, float partialTick){
        float ratio = minecraft.getWindow().getWidth() / (float)width;
        RenderSystem.setShaderTexture(0, LOCATION);
        blit(poseStack, stageX, stageY, 0, 0, 176, 81, 256, 256);

        if(stageActive){
            int slot = getStageIndex(mouseX, mouseY);
            if(slot >= 0){
                AbstractContainerScreen.renderSlotHighlight(poseStack, slot % 9 * 18 + stageX + 8, (slot / 9) * 18 + stageY + 22, 0);
            }
        }

        float ct = Math.max(0, Math.min(1f, (tick + partialTick - cursorTick) / 5f));
        cursorX = (float)EasingFunc.easeOutQuint(ct) * (activeSlot % 9 * 18 + stageX + 4 - lastCursorX) + lastCursorX;
        cursorY = (float)EasingFunc.easeOutQuint(ct) * ((activeSlot / 9) * 18 + stageY + 18 - lastCursorY) + lastCursorY;
        RenderSystem.setShaderTexture(0, LOCATION);
        blit(poseStack, (int)cursorX, (int)cursorY, 176, 0, 24, 24, 256, 256);

        Component component = new TranslatableComponent(stage.title());
        if(font.width(component) > 160){
            float T = (font.width(component) - 157f) / textSpeed + 80;
            float t = (tick + partialTick) % T;
            float pos = 0;
            if(t < 40) pos = 0;
            else if(t > T - 40) pos = font.width(component) - 157f;
            else pos = (t - 40) * textSpeed;
            RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            RenderSystem.enableScissor((int)((stageX + 8) * ratio), 0, (int)(160 * ratio), minecraft.getWindow().getHeight());
            font.drawShadow(poseStack, component, stageX + 9.5f - pos, stageY + 8.5f, stageActive ? 0xffffff : 0x404040);
            RenderSystem.disableScissor();
        } else {
            font.drawShadow(poseStack, component, stageX + 9.5f, stageY + 8.5f, stageActive ? 0xffffff : 0x404040);
        }
    }

    private void renderLevel(PoseStack poseStack, int mouseX, int mouseY, float partialTick){
        float ratio = minecraft.getWindow().getWidth() / (float)width;

        RenderSystem.setShaderTexture(0, LOCATION);
        blit(poseStack, levelX, levelY, 0, 81, 176, 110, 256, 256);

        if(activeScenario != null){
            Component component = new TranslatableComponent(activeScenario.title());
            if(font.width(component) > 160){
                float T = (font.width(component) - 157f) / textSpeed + 80;
                float t = (tick + partialTick) % T;
                float pos = 0;
                if(t < 40) pos = 0;
                else if(t > T - 40) pos = font.width(component) - 157f;
                else pos = (t - 40) * textSpeed;
                RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
                RenderSystem.enableScissor((int)((levelX + 8) * ratio), 0, (int)(160 * ratio), minecraft.getWindow().getHeight());
                font.drawShadow(poseStack, component, levelX + 9.5f - pos, levelY + 8.5f, !stageActive ? 0xffffff : 0x404040);
                RenderSystem.disableScissor();
            } else {
                font.drawShadow(poseStack, component, levelX + 9.5f, levelY + 8.5f, !stageActive ? 0xffffff : 0x404040);
            }
        }

    }

    @Override
    public void tick() {
        tick++;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
        boolean clickSound = false;
        boolean stageClicked = stageX <= pMouseX && pMouseX <= stageX + 176 && stageY <= pMouseY && pMouseY <= stageY + 81;
        boolean levelClicked = levelX <= pMouseX && pMouseY <= levelY + 176 && levelY <= pMouseY && pMouseY <= levelY + 110;

        if(stageActive && stageClicked) {
            int slot = getStageIndex(pMouseX, pMouseY);
            if(0 <= slot && slot < stage.scenarios().size()){
                cursorTick = tick;
                lastCursorX = cursorX;
                lastCursorY = cursorY;
                setActiveScenario(slot);
                clickSound = true;
            }
        } else if(!stageActive && levelClicked){

        } else if(stageClicked) {
            clickSound = true;
            stageActive = true;
        } else if(levelClicked) {
            clickSound = true;
            stageActive = false;
        }

        if(clickSound){
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return true;
    }

    private void setActiveScenario(int slot){
        activeSlot = slot;
        String key = 0 <= activeSlot && activeSlot < stage.scenarios().size() ? stage.scenarios().get(activeSlot) : "";
        activeScenario = RpgScenarioManager.instance.get(key);
    }

    private int getStageIndex(double mouseX, double mouseY){
        if(stageX + 7 <= mouseX && mouseX <= stageX + 169 && stageY + 21 <= mouseY && mouseY <= stageY + 75){
            int x = ((int)mouseX - stageX - 7) / 18;
            int y = ((int)mouseY - stageY - 21) / 18;
            if(0 <= x && x <= 8 && 0 <= y && y <= 2){
                return x + y * 9;
            }
        }
        return -1;
    }
}
