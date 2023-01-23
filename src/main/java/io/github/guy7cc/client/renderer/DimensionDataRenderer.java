package io.github.guy7cc.client.renderer;

import io.github.guy7cc.resource.DimensionData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class DimensionDataRenderer {
    public static void show(DimensionData data){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.gui.setTitle(new TextComponent(data.title()));
            minecraft.gui.setSubtitle(new TextComponent(data.subtitle()));
            minecraft.gui.setTimes(20, 70, 10);
        });
    }
}
