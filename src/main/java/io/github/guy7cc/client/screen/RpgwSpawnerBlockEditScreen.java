package io.github.guy7cc.client.screen;

import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
}
