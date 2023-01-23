package io.github.guy7cc.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.guy7cc.block.RpgStageBlock;
import io.github.guy7cc.block.entity.RpgStageBlockEntity;
import io.github.guy7cc.util.EasingFunc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RpgStageBlockEntityRenderer implements BlockEntityRenderer<RpgStageBlockEntity> {
    private ItemRenderer itemRenderer;

    public RpgStageBlockEntityRenderer(){
        Minecraft minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void render(RpgStageBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        BakedModel model = itemRenderer.getModel(sword, pBlockEntity.getLevel(), null, 0);


        pPoseStack.pushPose();

        float tick = pBlockEntity.getSwordTick() + (pBlockEntity.getSwordActive() ? pPartialTick : -pPartialTick);
        tick = Math.max(0, Math.min(20, tick));
        double ease = EasingFunc.easeInOutCubic(tick / 20d);

        pPoseStack.translate(0.5, 0.75 + ease * 0.3, 0.5);

        double theta = Math.PI * 4 * ease * ease;
        Quaternion q = Quaternion.fromXYZDegrees(new Vector3f(0, 0, 135));
        Vector3f axis = new Vector3f(1, -1, 0);
        axis.normalize();
        if(pBlockEntity.getBlockState().getValue(RpgStageBlock.FACING).getAxis() == Direction.Axis.X)
            q.mul(new Quaternion(axis, (float)Math.PI / 2, false));
        q.mul(new Quaternion(axis, (float)theta, false));
        pPoseStack.mulPose(q);

        itemRenderer.render(new ItemStack(Items.DIAMOND_SWORD), ItemTransforms.TransformType.FIXED, true, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, model);
        pPoseStack.popPose();
    }
}
