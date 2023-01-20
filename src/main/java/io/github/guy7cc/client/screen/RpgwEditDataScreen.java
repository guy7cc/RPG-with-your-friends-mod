package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundEditDataPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;

public class RpgwEditDataScreen extends Screen {
    private BlockPos pos;
    private String data;

    private EditBox edit;
    private Button send;

    public RpgwEditDataScreen(BlockPos pos, String original){
        super(TextComponent.EMPTY);
        this.pos = pos;
        data = original;
    }

    @Override
    protected void init(){
        edit = new EditBox(font, width / 2 - 100, height / 2 - 10, 200, 20, TextComponent.EMPTY);
        edit.setValue(data);
        edit.setResponder(str -> data = str);
        send = new Button(width / 2 - 50, height / 2 + 20, 100, 20, new TextComponent("Save"), button -> {
            RpgwMessageManager.sendToServer(new ServerboundEditDataPacket(pos, data));
            minecraft.setScreen(null);
        });

        addRenderableWidget(edit);
        addRenderableWidget(send);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
