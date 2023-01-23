package io.github.guy7cc.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.resource.RpgScenarioCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class RpgScenarioRenderer {

    public static class Feature {

    }

    public static class Condition {
        public static void render(RpgScenarioCondition.MaxPlayer condition, PoseStack poseStack, int x, int y, int width) {
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> sequences = ComponentRenderUtils.wrapComponents(new TranslatableComponent("rpgscenario.condition.maxPlayer.require").append(Integer.toString(condition.getMax())), width, Minecraft.getInstance().font);
            for(FormattedCharSequence s : sequences){
                font.drawShadow(poseStack, s, x, y, 0xffffff);
            }
        }

        public static void render(RpgScenarioCondition.PassedScenario condition, PoseStack poseStack, int x, int y, int width){
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> sequences = ComponentRenderUtils.wrapComponents(condition.getRequirement(), width, Minecraft.getInstance().font);
            for(FormattedCharSequence s : sequences){
                font.drawShadow(poseStack, s, x, y, 0xffffff);
            }
        }

        public static void render(RpgScenarioCondition.AllowedItems condition, PoseStack poseStack, int x, int y, int width){
            throw new NotImplementedException();
        }

        public static void render(RpgScenarioCondition.BannedItems condition, PoseStack poseStack, int x, int y, int width){
            throw new NotImplementedException();
        }
    }

}
