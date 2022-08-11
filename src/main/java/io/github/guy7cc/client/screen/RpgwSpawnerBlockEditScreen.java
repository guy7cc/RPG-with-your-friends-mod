package io.github.guy7cc.client.screen;

import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

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

    protected RpgwSpawnerBlockEditScreen(Component pTitle, RpgwSpawnerBlockEntity blockEntity) {
        super(pTitle);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        Font font = Minecraft.getInstance().font;
        this.minXSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 - 87, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.x"));
        this.minYSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 - 65, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.y"));
        this.minZSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 - 43, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.z"));
        this.maxXSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 - 21, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.x"));
        this.maxYSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 + 1 , 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.y"));
        this.maxZSpawnAreaEdit = new EditBox(font, this.width / 2 - 75, this.height / 2 + 23, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.z"));
        this.minXPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 - 87, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.x"));
        this.minYPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 - 65, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.y"));
        this.minZPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 - 43, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.min.z"));
        this.maxXPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 - 21, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.x"));
        this.maxYPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 + 1 , 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.y"));
        this.maxZPlayerAreaEdit = new EditBox(font, this.width / 2 - 13, this.height / 2 + 23, 60, 20, new TranslatableComponent("gui.rpgwmod.editScreen.max.z"));
    }
}
