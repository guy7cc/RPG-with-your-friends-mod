package io.github.guy7cc.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.guy7cc.block.entity.RpgwSpawnerBlockEntity;
import io.github.guy7cc.item.RpgwItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RpgwSpawnerBlockEntityRenderer implements BlockEntityRenderer<RpgwSpawnerBlockEntity> {

    @Override
    public void render(RpgwSpawnerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Player player = Minecraft.getInstance().player;
        if(player.getMainHandItem().is(RpgwItems.DEBUG_WRENCH.get()) || player.getOffhandItem().is(RpgwItems.DEBUG_WRENCH.get())){
            BlockPos pos = pBlockEntity.getBlockPos();
            AABB spawnArea = pBlockEntity.getBaseSpawner().getSpawnArea();
            AABB playerArea = pBlockEntity.getBaseSpawner().getPlayerArea();
            VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(pPoseStack, vertexconsumer,
                    spawnArea.minX - pos.getX(),
                    spawnArea.minY - pos.getY(),
                    spawnArea.minZ - pos.getZ(),
                    spawnArea.maxX - pos.getX(),
                    spawnArea.maxY - pos.getY(),
                    spawnArea.maxZ - pos.getZ(),
                    0.4140625F, 0.3515625F, 0.80078125F, 1.0F,
                    0.4140625F, 0.3515625F, 0.80078125F);
            LevelRenderer.renderLineBox(pPoseStack, vertexconsumer,
                    playerArea.minX - pos.getX(),
                    playerArea.minY - pos.getY(),
                    playerArea.minZ - pos.getZ(),
                    playerArea.maxX - pos.getX(),
                    playerArea.maxY - pos.getY(),
                    playerArea.maxZ - pos.getZ(),
                    0.9765625F, 0.5F, 0.4453125F, 1.0F,
                    0.9765625F, 0.5F, 0.4453125F);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(RpgwSpawnerBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
