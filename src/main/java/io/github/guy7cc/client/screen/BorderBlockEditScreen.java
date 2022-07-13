package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.block.entity.AbstractBorderBlockEntity;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundEditBorderPacket;
import io.github.guy7cc.rpg.Border;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.Consumer;

public class BorderBlockEditScreen extends Screen {
    private AbstractBorderBlockEntity borderBE;

    private EditBox negativeXEdit;
    private EditBox negativeZEdit;
    private EditBox positiveXEdit;
    private EditBox positiveZEdit;
    private Button saveButton;

    public BorderBlockEditScreen(Component pTitle, AbstractBorderBlockEntity borderBE) {
        super(pTitle);
        this.borderBE = borderBE;
    }

    @Override
    protected void init() {
        Font font = Minecraft.getInstance().font;
        this.negativeXEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 - 43, 150, 20, new TranslatableComponent("gui.rpgwmod.borderBlockEdit.negative.x"));
        this.negativeZEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 - 21, 150, 20, new TranslatableComponent("gui.rpgwmod.borderBlockEdit.negative.z"));
        this.positiveXEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 + 1,  150, 20, new TranslatableComponent("gui.rpgwmod.borderBlockEdit.positive.x"));
        this.positiveZEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 + 23, 150, 20, new TranslatableComponent("gui.rpgwmod.borderBlockEdit.positive.z"));

        resetValues();

        Consumer<String> responder = str -> {
            this.saveButton.active = validValues();
        };
        this.negativeXEdit.setResponder(responder);
        this.negativeZEdit.setResponder(responder);
        this.positiveXEdit.setResponder(responder);
        this.positiveZEdit.setResponder(responder);

        this.saveButton = new Button(this.width / 2 - 50, this.height / 2 + 55, 100, 20, new TranslatableComponent("structure_block.mode.save"), button -> {
            if(validValues()){
                double negativeX = Double.parseDouble(this.negativeXEdit.getValue());
                double positiveX = Double.parseDouble(this.positiveXEdit.getValue());
                double negativeZ = Double.parseDouble(this.negativeZEdit.getValue());
                double positiveZ = Double.parseDouble(this.positiveZEdit.getValue());
                BlockPos blockPos = borderBE.getBlockPos();

                Border border = new Border(blockPos.getX() - negativeX, blockPos.getX() + positiveX + 1, blockPos.getZ() - negativeZ, blockPos.getZ() + positiveZ + 1);
                RpgwMessageManager.sendToServer(new ServerboundEditBorderPacket(border, blockPos));
                minecraft.setScreen(null);
            } else {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.player.displayClientMessage(new TranslatableComponent("gui.rpgwmod.borderBlockEdit.invalid"), false);
                minecraft.setScreen(null);
            }
        });

        this.addRenderableWidget(this.negativeXEdit);
        this.addRenderableWidget(this.negativeZEdit);
        this.addRenderableWidget(this.positiveXEdit);
        this.addRenderableWidget(this.positiveZEdit);
        this.addRenderableWidget(this.saveButton);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderTexts(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void renderTexts(PoseStack poseStack){
        Font font = Minecraft.getInstance().font;
        Component component;
        component = this.title;
        font.draw(poseStack, component, this.width / 2 - font.width(component) / 2, this.height / 2 - 66, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.borderBlockEdit.negative.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 - 36, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.borderBlockEdit.negative.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 - 14, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.borderBlockEdit.positive.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 + 8, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.borderBlockEdit.positive.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 + 31, 0xffffff);
    }

    @Override
    public void tick() {
        negativeXEdit.tick();
        negativeZEdit.tick();
        positiveXEdit.tick();
        positiveZEdit.tick();
    }

    public void setBorder(Border border){
        this.borderBE.border = border;
        resetValues();
    }

    private void resetValues(){
        BlockPos pos = borderBE.getBlockPos();
        this.negativeXEdit.setValue(Double.toString(pos.getX() - borderBE.border.minX));
        this.negativeZEdit.setValue(Double.toString(pos.getZ() - borderBE.border.minZ));
        this.positiveXEdit.setValue(Double.toString(borderBE.border.maxX - pos.getX() - 1));
        this.positiveZEdit.setValue(Double.toString(borderBE.border.maxZ - pos.getZ() - 1));
    }

    private boolean validValues(){
        String[] valueArray = new String[]{ negativeXEdit.getValue(), negativeZEdit.getValue(), positiveXEdit.getValue(), positiveZEdit.getValue() };
        for(String str : valueArray){
            try{
                double value = Double.parseDouble(str);
                if(value < 0D) return false;
            } catch(NumberFormatException e){
                return false;
            }
        }
        return true;
    }
}
