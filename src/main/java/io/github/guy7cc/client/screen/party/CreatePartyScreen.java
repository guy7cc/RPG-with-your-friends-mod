package io.github.guy7cc.client.screen.party;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundManagePartyPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class CreatePartyScreen extends Screen {
    public static final ResourceLocation CREATE_PARTY_MENU_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/create_party_menu.png");

    private EditBox nameEdit;
    private Button createButton;
    private Button cancelButton;

    private int leftPos;
    private int topPos;
    private int imageWidth = 200;
    private int imageHeight = 64;
    private int titleLabelX;
    private int titleLabelY;

    public CreatePartyScreen() {
        super(new TranslatableComponent("gui.rpgwmod.createPartyMenu"));
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.titleLabelX = leftPos + 5;
        this.titleLabelY = topPos + 5;

        this.nameEdit = new EditBox(this.font, this.leftPos + 18, this.topPos + 18, 164, 20, new TextComponent(""));
        this.createButton = new Button(this.leftPos + 30, this.topPos + 40, 60, 20, new TranslatableComponent("gui.rpgwmod.createPartyMenu.create"), button -> {
            RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.CREATE, nameEdit.getValue().isEmpty() ? "No Name" : nameEdit.getValue(), Minecraft.getInstance().player.getUUID(), 0));
            Minecraft.getInstance().setScreen(new PartyMenuScreen());
        });
        this.cancelButton = new Button(this.leftPos + 110, this.topPos + 40, 60, 20, new TranslatableComponent("gui.rpgwmod.createPartyMenu.cancel"), button -> {
            Minecraft.getInstance().setScreen(new PartyMenuScreen());
        });
        addRenderableWidget(this.nameEdit);
        addRenderableWidget(this.createButton);
        addRenderableWidget(this.cancelButton);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack);
        renderTitle(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    private void renderBg(PoseStack pPoseStack){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CREATE_PARTY_MENU_LOCATION);
        this.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 64);
    }

    private void renderTitle(PoseStack pPoseStack){
        this.font.draw(pPoseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }
}
