package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class ScreenUtil {
    public static boolean closeIfInventoryKeyPressed(Minecraft minecraft, int pKeyCode, int pScanCode){
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
        if(minecraft.options.keyInventory.isActiveAndMatches(mouseKey)){
            minecraft.setScreen(null);
            return true;
        }
        return false;
    }
}
