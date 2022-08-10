package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.block.entity.IBorderBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class BorderBlockEditScreen extends Screen {
    private BlockEntity be;
    private IBorderBlockEntity asBorder;

    private EditBox minXEdit;
    private EditBox maxXEdit;
    private EditBox minZEdit;
    private EditBox maxZEdit;
    private Button saveButton;

    public BorderBlockEditScreen(Component pTitle, BlockEntity borderBE) {
        super(pTitle);
        this.be = borderBE;
        if(borderBE instanceof IBorderBlockEntity){
            this.asBorder = (IBorderBlockEntity) borderBE;
        }
    }

    @Override
    protected void init() {
        if(this.asBorder == null){
            Minecraft.getInstance().setScreen(null);
            return;
        }

        Font font = Minecraft.getInstance().font;
        this.minXEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 - 43, 150, 20, new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.negative.x"));
        this.maxXEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 - 21,  150, 20, new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.positive.x"));
        this.minZEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 + 1, 150, 20, new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.negative.z"));
        this.maxZEdit = new EditBox(font, this.width / 2 - 50, this.height / 2 + 23, 150, 20, new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.positive.z"));

        resetValues();

        Consumer<String> responder = str -> {
            this.saveButton.active = validValues();
        };
        this.minXEdit.setResponder(responder);
        this.maxXEdit.setResponder(responder);
        this.minZEdit.setResponder(responder);
        this.maxZEdit.setResponder(responder);

        this.saveButton = new Button(this.width / 2 - 50, this.height / 2 + 55, 100, 20, new TranslatableComponent("structure_block.mode.save"), button -> {
            if(validValues()){
                double negativeX = Double.parseDouble(this.minXEdit.getValue());
                double negativeZ = Double.parseDouble(this.minZEdit.getValue());
                double positiveX = Double.parseDouble(this.maxXEdit.getValue());
                double positiveZ = Double.parseDouble(this.maxZEdit.getValue());
                BlockPos blockPos = be.getBlockPos();

                Border border = new Border(negativeX, positiveX, negativeZ, positiveZ);
                RpgwMessageManager.sendToServer(new ServerboundEditBorderPacket(border, blockPos));
                minecraft.setScreen(null);
            } else {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.player.displayClientMessage(new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.invalid"), false);
                minecraft.setScreen(null);
            }
        });

        this.addRenderableWidget(this.minXEdit);
        this.addRenderableWidget(this.maxXEdit);
        this.addRenderableWidget(this.minZEdit);
        this.addRenderableWidget(this.maxZEdit);
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
        component = new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.negative.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 - 36, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.positive.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 - 14, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.negative.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 + 8, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmodmod.borderBlockEdit.positive.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 60, this.height / 2 + 31, 0xffffff);
    }

    @Override
    public void tick() {
        minXEdit.tick();
        maxXEdit.tick();
        minZEdit.tick();
        maxZEdit.tick();
    }

    public void setBorder(Border border){
        this.asBorder.setBorder(border);
        resetValues();
    }

    private void resetValues(){
        Border border = asBorder.getBorder();
        this.minXEdit.setValue(Double.toString(border.minX));
        this.maxXEdit.setValue(Double.toString(border.maxX));
        this.minZEdit.setValue(Double.toString(border.minZ));
        this.maxZEdit.setValue(Double.toString(border.maxZ));
    }

    private boolean validValues(){
        String[] strArray = new String[]{ minXEdit.getValue(), maxXEdit.getValue(), minZEdit.getValue(), maxZEdit.getValue() };
        Double[] valueArray = new Double[4];
        for(int i = 0; i < 4; i++){
            try{
                valueArray[i] = Double.parseDouble(strArray[i]);
            } catch(NumberFormatException e){
                return false;
            }
        }
        return valueArray[0] <= valueArray[1] - 1 && valueArray[2] <= valueArray[3] - 1;
    }
}
