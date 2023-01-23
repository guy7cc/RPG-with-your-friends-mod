package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.client.renderer.RpgScenarioFeatureRenderer;
import io.github.guy7cc.network.ClientboundSetRpgStagePacket;
import io.github.guy7cc.resource.*;
import io.github.guy7cc.util.EasingFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RpgStageScreen extends Screen {
    private static final ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/rpg_stage.png");
    private static final float textSpeed = 0.25f;
    private static final TextComponent ERROR_TEXT = new TextComponent("An error has occurred.");

    private ResourceLocation location;
    private RpgStage stage;
    private boolean packetReceived = false;

    private int stageX;
    private int stageY;
    private int scenarioX;
    private int scenarioY;
    private boolean stageActive = true;

    private int activeSlot = 0;
    private RpgScenario activeScenario;
    private float cursorX;
    private float cursorY;
    private float lastCursorX;
    private float lastCursorY;
    private float scroll = 0;

    private Component stageTitle;
    private Component scenarioTitle;
    private List<FormattedCharSequence> scenarioInfo;

    private int tick = 0;
    private int cursorTick = 0;

    public RpgStageScreen(RpgStageBlockEntity be) {
        super(TextComponent.EMPTY);
        location = be.getStage();
    }

    @Override
    protected void init() {
        stageX = width / 2 - 104;
        stageY = height / 2 - 78;
        scenarioX = width / 2 - 72;
        scenarioY = height / 2 - 33;
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

        if(packetReceived){
            if(stageActive){
                renderScenario(pPoseStack, pMouseX, pMouseY, pPartialTick);
                renderStage(pPoseStack, pMouseX, pMouseY, pPartialTick);
            } else {
                renderStage(pPoseStack, pMouseX, pMouseY, pPartialTick);
                renderScenario(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }
        }
    }

    private void renderStage(PoseStack poseStack, int mouseX, int mouseY, float partialTick){
        float ratio = minecraft.getWindow().getWidth() / (float)width;
        float shaderColor = stageActive ? 1f : 0.5f;

        RenderSystem.setShaderTexture(0, LOCATION);
        RenderSystem.setShaderColor(shaderColor, shaderColor, shaderColor, 1f);
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

        if(font.width(stageTitle) > 160){
            float T = (font.width(stageTitle) - 157f) / textSpeed + 80;
            float t = (tick + partialTick) % T;
            float pos = 0;
            if(t < 40) pos = 0;
            else if(t > T - 40) pos = font.width(stageTitle) - 157f;
            else pos = (t - 40) * textSpeed;
            RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            RenderSystem.enableScissor((int)((stageX + 8) * ratio), 0, (int)(160 * ratio), minecraft.getWindow().getHeight());
            font.drawShadow(poseStack, stageTitle, stageX + 9.5f - pos, stageY + 8.5f, stageActive ? 0xffffff : 0x404040);
            RenderSystem.disableScissor();
        } else {
            font.drawShadow(poseStack, stageTitle, stageX + 9.5f, stageY + 8.5f, stageActive ? 0xffffff : 0x404040);
        }
    }

    private void renderScenario(PoseStack poseStack, int mouseX, int mouseY, float partialTick){
        float ratio = minecraft.getWindow().getWidth() / (float)width;
        float shaderColor = !stageActive ? 1f : 0.5f;

        RenderSystem.setShaderTexture(0, LOCATION);
        RenderSystem.setShaderColor(shaderColor, shaderColor, shaderColor, 1f);
        blit(poseStack, scenarioX, scenarioY, 0, 81, 176, 110, 256, 256);

        if(!RpgScenarioManager.instance.containsKey(stage.scenarios().get(activeSlot))) return;

        if(activeScenario != null){
            if(font.width(scenarioTitle) > 160){
                float T = (font.width(scenarioTitle) - 157f) / textSpeed + 80;
                float t = (tick + partialTick) % T;
                float pos = 0;
                if(t < 40) pos = 0;
                else if(t > T - 40) pos = font.width(scenarioTitle) - 157f;
                else pos = (t - 40) * textSpeed;
                RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
                RenderSystem.enableScissor((int)((scenarioX + 8) * ratio), minecraft.getWindow().getHeight() - (int)((scenarioY + 18) * ratio), (int)(160 * ratio), (int)(10 * ratio));
                font.drawShadow(poseStack, scenarioTitle, scenarioX + 9.5f - pos, scenarioY + 8.5f, !stageActive ? 0xffffff : 0x404040);
                RenderSystem.disableScissor();
            } else {
                font.drawShadow(poseStack, scenarioTitle, scenarioX + 9.5f, scenarioY + 8.5f, !stageActive ? 0xffffff : 0x404040);
            }
            RenderSystem.enableScissor((int)((scenarioX + 8) * ratio), minecraft.getWindow().getHeight() - (int)((scenarioY + 103) * ratio), (int)(160 * ratio), (int)(81 * ratio));
            RenderSystem.setShaderColor(shaderColor, shaderColor, shaderColor, 1f);
            float x = scenarioX + 168 - activeScenario.features().size() * 11;
            RpgScenarioFeature forToolTip = null;
            for(RpgScenarioFeature feature : activeScenario.features()){
                RpgScenarioFeatureRenderer.render(feature, poseStack, (int)x, scenarioY + 92);
                if(x <= mouseX && mouseX <= x + 10 && scenarioY + 92 <= mouseY && mouseY <= scenarioY + 102) forToolTip = feature;
                x += 11f;
            }
            float y = scenarioY + 24f - scroll;
            for(FormattedCharSequence seq : scenarioInfo){
                font.drawShadow(poseStack, seq, scenarioX + 9.5f, y, !stageActive ? 0xffffff : 0x404040);
                y += 10f;
            }
            RenderSystem.disableScissor();
            Style style = getClickedComponentStyleAt(mouseX, mouseY);
            this.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
            if(forToolTip != null) renderTooltip(poseStack, forToolTip.getToolTip(), mouseX, mouseY);
        }


    }

    @Override
    public void tick() {
        tick++;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
        if(super.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        return ScreenUtil.closeIfInventoryKeyPressed(minecraft, pKeyCode, pScanCode);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
        if(!packetReceived) return false;

        boolean stageClicked = stageX <= pMouseX && pMouseX <= stageX + 176 && stageY <= pMouseY && pMouseY <= stageY + 81;
        boolean scenarioClicked = scenarioX <= pMouseX && pMouseX <= scenarioX + 176 && scenarioY <= pMouseY && pMouseY <= scenarioY + 110;

        if(stageActive && stageClicked) {
            int slot = getStageIndex(pMouseX, pMouseY);
            if(0 <= slot && slot < stage.scenarios().size()){
                cursorTick = tick;
                lastCursorX = cursorX;
                lastCursorY = cursorY;
                scroll = 0;
                setActiveScenario(slot);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else if(!stageActive && scenarioClicked){
            Style style = getClickedComponentStyleAt(pMouseX, pMouseY);
            if (style != null && handleComponentClicked(style)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else if(stageClicked) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            stageActive = true;
            return true;
        } else if(scenarioClicked) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            stageActive = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(!packetReceived) return false;

        boolean onScenarioInfo = scenarioX + 7 <= pMouseX && pMouseX <= scenarioX + 169 && scenarioY + 21 <= pMouseY && pMouseY <= scenarioY + 104;
        if(!stageActive && onScenarioInfo){
            int max = Math.max(0, scenarioInfo.size() * 10 - 78);
            scroll = Math.max(0, Math.min(max, scroll - (int)pDelta * 2));
        }
        return true;
    }

    private void setActiveScenario(int slot){
        if(slot < 0 || stage.scenarios().size() <= slot) return;
        activeSlot = slot;
        ResourceLocation key = stage.scenarios().get(activeSlot);
        activeScenario = RpgScenarioManager.instance.getOrDefault(key);
        try{
            stageTitle = Component.Serializer.fromJsonLenient(stage.title());
        } catch(Exception exception){
            RpgwMod.LOGGER.error("An error has occurred while parsing json to stage title component.");
            stageTitle = ERROR_TEXT;
        }
        try{
            scenarioTitle = Component.Serializer.fromJsonLenient(activeScenario.title());
        } catch(Exception exception){
            RpgwMod.LOGGER.error("An error has occurred while parsing json to scenario title component.");
            scenarioTitle = ERROR_TEXT;
        }
        try{
            scenarioInfo = ComponentRenderUtils.wrapComponents(Component.Serializer.fromJsonLenient(activeScenario.info()), 157, Minecraft.getInstance().font);
        } catch(Exception exception){
            RpgwMod.LOGGER.error("An error has occurred while parsing json to scenario info component.");
            scenarioInfo = List.of(Language.getInstance().getVisualOrder(ERROR_TEXT));
        }
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

    public Style getClickedComponentStyleAt(double pMouseX, double pMouseY){
        if(scenarioX + 8 <= pMouseX && pMouseX <= scenarioX + 168 && scenarioY + 22 <= pMouseY && pMouseY <= scenarioY + 103){
            int index = (int)((scroll + pMouseY - scenarioY - 22) / 10d);
            if(0 <= index && index < scenarioInfo.size())
                return minecraft.font.getSplitter().componentStyleAtWidth(scenarioInfo.get(index), (int)(pMouseX - scenarioX - 9.5d));
        }
        return null;
    }

    public void handlePacket(ClientboundSetRpgStagePacket packet){
        if(packet.getLocation().equals(location)){
            packetReceived = true;
            stage = RpgStageManager.instance.getOrDefault(location);
            setActiveScenario(0);
        }
    }
}
