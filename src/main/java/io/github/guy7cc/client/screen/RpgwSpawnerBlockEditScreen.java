package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.block.entity.RpgwBaseSpawner;
import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundEditRpgwSpawnerBlockPacket;
import io.github.guy7cc.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public class RpgwSpawnerBlockEditScreen extends Screen {
    private RpgwSpawnerBlockEntity blockEntity;

    private EditBox minXSpawnAreaEdit;
    private EditBox minYSpawnAreaEdit;
    private EditBox minZSpawnAreaEdit;
    private EditBox maxXSpawnAreaEdit;
    private EditBox maxYSpawnAreaEdit;
    private EditBox maxZSpawnAreaEdit;
    private EditBox minXPlayerAreaEdit;
    private EditBox minYPlayerAreaEdit;
    private EditBox minZPlayerAreaEdit;
    private EditBox maxXPlayerAreaEdit;
    private EditBox maxYPlayerAreaEdit;
    private EditBox maxZPlayerAreaEdit;

    private EditBox entityIdBox;
    private EditBox minDelayEdit;
    private EditBox maxDelayEdit;
    private EditBox remainEdit;
    private EditBox maxAliveEdit;

    private Button saveButton;

    private RpgwBaseSpawner.Type mode;

    public RpgwSpawnerBlockEditScreen(Component pTitle, RpgwSpawnerBlockEntity blockEntity) {
        super(pTitle);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        Font font = Minecraft.getInstance().font;
        Component message = new TextComponent("");
        this.minXSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 - 65, 60, 20, message);
        this.minYSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 - 43, 60, 20, message);
        this.minZSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 - 21, 60, 20, message);
        this.maxXSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 + 1 , 60, 20, message);
        this.maxYSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 + 23, 60, 20, message);
        this.maxZSpawnAreaEdit = new EditBox(font, this.width / 2 - 105, this.height / 2 + 45, 60, 20, message);
        this.minXPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 - 65, 60, 20, message);
        this.minYPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 - 43, 60, 20, message);
        this.minZPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 - 21, 60, 20, message);
        this.maxXPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 + 1 , 60, 20, message);
        this.maxYPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 + 23, 60, 20, message);
        this.maxZPlayerAreaEdit = new EditBox(font, this.width / 2 - 43, this.height / 2 + 45, 60, 20, message);
        this.entityIdBox = new EditBox(font, this.width / 2 + 47, this.height / 2 - 65, 90, 20, message);
        this.minDelayEdit = new EditBox(font, this.width / 2 + 47, this.height / 2 - 21, 30, 20, message);
        this.maxDelayEdit = new EditBox(font, this.width / 2 + 87, this.height / 2 - 21, 30, 20, message);

        this.entityIdBox.setEditable(false);

        this.setVisible();
        this.resetValues();

        Consumer<String> responder = str -> {
            this.saveButton.active = validValues();
        };
        this.minXSpawnAreaEdit.setResponder(responder);
        this.minYSpawnAreaEdit.setResponder(responder);
        this.minZSpawnAreaEdit.setResponder(responder);
        this.maxXSpawnAreaEdit.setResponder(responder);
        this.maxYSpawnAreaEdit.setResponder(responder);
        this.maxZSpawnAreaEdit.setResponder(responder);
        this.minXPlayerAreaEdit.setResponder(responder);
        this.minYPlayerAreaEdit.setResponder(responder);
        this.minZPlayerAreaEdit.setResponder(responder);
        this.maxXPlayerAreaEdit.setResponder(responder);
        this.maxYPlayerAreaEdit.setResponder(responder);
        this.maxZPlayerAreaEdit.setResponder(responder);

        this.saveButton = new Button(this.width / 2 - 50, this.height / 2 + 75, 100, 20, new TranslatableComponent("structure_block.mode.save"), button -> {
            if(validValues()){
                AABB spawnArea = new AABB(
                        Double.parseDouble(this.minXSpawnAreaEdit.getValue()),
                        Double.parseDouble(this.minYSpawnAreaEdit.getValue()),
                        Double.parseDouble(this.minZSpawnAreaEdit.getValue()),
                        Double.parseDouble(this.maxXSpawnAreaEdit.getValue()),
                        Double.parseDouble(this.maxYSpawnAreaEdit.getValue()),
                        Double.parseDouble(this.maxZSpawnAreaEdit.getValue())
                );
                AABB playerArea = new AABB(
                        Double.parseDouble(this.minXPlayerAreaEdit.getValue()),
                        Double.parseDouble(this.minYPlayerAreaEdit.getValue()),
                        Double.parseDouble(this.minZPlayerAreaEdit.getValue()),
                        Double.parseDouble(this.maxXPlayerAreaEdit.getValue()),
                        Double.parseDouble(this.maxYPlayerAreaEdit.getValue()),
                        Double.parseDouble(this.maxZPlayerAreaEdit.getValue())
                );
                CompoundTag tag = new CompoundTag();
                tag.putInt("SpawnerType", 0);
                CompoundTag spawnerTag = new CompoundTag();
                spawnerTag.put("SpawnArea", Util.saveAABB(spawnArea));
                spawnerTag.put("PlayerArea", Util.saveAABB(playerArea));
                tag.put("Spawner", spawnerTag);
                RpgwMessageManager.sendToServer(new ServerboundEditRpgwSpawnerBlockPacket(blockEntity.getBlockPos(), tag));
                minecraft.setScreen(null);
            } else {
                minecraft.player.displayClientMessage(new TranslatableComponent("gui.rpgwmod.editScreen.invalid"), false);
                minecraft.setScreen(null);
            }
        });

        this.addRenderableWidget(this.minXSpawnAreaEdit);
        this.addRenderableWidget(this.minYSpawnAreaEdit);
        this.addRenderableWidget(this.minZSpawnAreaEdit);
        this.addRenderableWidget(this.maxXSpawnAreaEdit);
        this.addRenderableWidget(this.maxYSpawnAreaEdit);
        this.addRenderableWidget(this.maxZSpawnAreaEdit);
        this.addRenderableWidget(this.minXPlayerAreaEdit);
        this.addRenderableWidget(this.minYPlayerAreaEdit);
        this.addRenderableWidget(this.minZPlayerAreaEdit);
        this.addRenderableWidget(this.maxXPlayerAreaEdit);
        this.addRenderableWidget(this.maxYPlayerAreaEdit);
        this.addRenderableWidget(this.maxZPlayerAreaEdit);
        this.addRenderableWidget(this.entityIdBox);
        this.addRenderableWidget(this.minDelayEdit);
        this.addRenderableWidget(this.maxDelayEdit);
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
        font.draw(poseStack, component, this.width / 2 - font.width(component) / 2, this.height / 2 - 100, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.spawnArea");
        font.draw(poseStack, component, this.width / 2 - font.width(component) / 2 - 75, this.height / 2 - 80, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.playerArea");
        font.draw(poseStack, component, this.width / 2 - font.width(component) / 2 - 13, this.height / 2 - 80, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.min.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 - 58, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.min.y");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 - 36, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.min.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 - 14, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.max.x");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 + 8, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.max.y");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 + 30, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.max.z");
        font.draw(poseStack, component, this.width / 2 - font.width(component) - 115, this.height / 2 + 52, 0xffffff);
        component = new TranslatableComponent("gui.rpgwmod.editScreen.delay");
        font.draw(poseStack, component, this.width / 2 + 47, this.height / 2 - 36, 0xffffff);
        component = new TextComponent("..");
        font.draw(poseStack, component, this.width / 2 + 80, this.height / 2 - 18, 0xffffff);
    }

    private void setVisible(){
        RpgwBaseSpawner spawner = this.blockEntity.getBaseSpawner();
        switch(spawner.getType()){
            case SINGLE:
                break;
            case UNIQUE:
                this.entityIdBox.setVisible(false);
                break;
            default:
                break;
        }
    }

    private void resetValues(){
        RpgwBaseSpawner spawner = this.blockEntity.getBaseSpawner();
        AABB spawnArea = spawner.getSpawnArea();
        AABB playerArea = spawner.getPlayerArea();
        this.minXSpawnAreaEdit.setValue(Double.toString(spawnArea.minX));
        this.minYSpawnAreaEdit.setValue(Double.toString(spawnArea.minY));
        this.minZSpawnAreaEdit.setValue(Double.toString(spawnArea.minZ));
        this.maxXSpawnAreaEdit.setValue(Double.toString(spawnArea.maxX));
        this.maxYSpawnAreaEdit.setValue(Double.toString(spawnArea.maxY));
        this.maxZSpawnAreaEdit.setValue(Double.toString(spawnArea.maxZ));
        this.minXPlayerAreaEdit.setValue(Double.toString(playerArea.minX));
        this.minYPlayerAreaEdit.setValue(Double.toString(playerArea.minY));
        this.minZPlayerAreaEdit.setValue(Double.toString(playerArea.minZ));
        this.maxXPlayerAreaEdit.setValue(Double.toString(playerArea.maxX));
        this.maxYPlayerAreaEdit.setValue(Double.toString(playerArea.maxY));
        this.maxZPlayerAreaEdit.setValue(Double.toString(playerArea.maxZ));

        switch(spawner.getType()){
            case SINGLE:
                this.entityIdBox.setValue(((RpgwBaseSpawner.Single) spawner).getEntityId().getRegistryName().toString());
                break;
            default:
                minecraft.setScreen(null);
        }
    }

    private boolean validValues(){
        String[] strArray = new String[]{
                this.minXSpawnAreaEdit.getValue(),
                this.minYSpawnAreaEdit.getValue(),
                this.minZSpawnAreaEdit.getValue(),
                this.maxXSpawnAreaEdit.getValue(),
                this.maxYSpawnAreaEdit.getValue(),
                this.maxZSpawnAreaEdit.getValue(),
                this.minXPlayerAreaEdit.getValue(),
                this.minYPlayerAreaEdit.getValue(),
                this.minZPlayerAreaEdit.getValue(),
                this.maxXPlayerAreaEdit.getValue(),
                this.maxYPlayerAreaEdit.getValue(),
                this.maxZPlayerAreaEdit.getValue()
        };
        Double[] valueArray = new Double[12];
        for(int i = 0; i < 12; i++){
            try{
                valueArray[i] = Double.parseDouble(strArray[i]);
            } catch(NumberFormatException e){
                return false;
            }
        }
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                if(valueArray[i * 6 + j] > valueArray[i * 6 + j + 3]) return false;
            }
        }
        return true;
    }
}
