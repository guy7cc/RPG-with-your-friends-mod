package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.resource.TraderDataElement;
import io.github.guy7cc.resource.TraderData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class TraderScreen extends Screen {
    public static final int NODE_BOX_HEIGHT = 150;

    private TraderData data;
    private int activeTab = 0;
    private int scrollHeight = 0;
    private int nodeX;
    private int nodeY;

    public TraderScreen(TraderData data) {
        super(new TextComponent(""));
        this.data = data;
        for(; activeTab < 3; activeTab++){
            if(!data.getList(activeTab).isEmpty()) break;
        }
        activeTab %= 3;
    }

    @Override
    protected void init() {
        nodeX = (width - TraderDataElement.WIDTH) / 2;
        nodeY = height / 2 - 100;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

    }

    private void renderNodes(PoseStack poseStack){
        List<? extends TraderDataElement> list = data.getList(activeTab);
        int i = scrollHeight / TraderDataElement.HEIGHT;
        int y = scrollHeight % TraderDataElement.HEIGHT;
        y = y > 0 ? y - TraderDataElement.HEIGHT : y;
        for(; i < list.size() && y < NODE_BOX_HEIGHT; i++){
            list.get(i).render(itemRenderer, nodeX, nodeY + y);
        }
    }
}
