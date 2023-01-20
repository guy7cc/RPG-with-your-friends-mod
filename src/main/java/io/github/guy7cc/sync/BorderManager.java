package io.github.guy7cc.sync;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.guy7cc.network.ClientboundSyncBorderPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.rpg.Border;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class BorderManager {
    private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");

    public static Border clientBorder;

    private static final Map<UUID, Border> map = new HashMap<>();

    public static void applyIfAbsent(ServerPlayer player, Border border){
        if(!map.containsKey(player.getUUID())){
            map.put(player.getUUID(), border);
            onChange(player);
        }
    }

    public static void remove(MinecraftServer server, int id){
        for(UUID uuid : map.keySet()){
            if(map.get(uuid).id == id) map.remove(uuid);
        }
        for(ServerPlayer player : server.getPlayerList().getPlayers()){
            onChange(player);
        }
    }

    public static void removeIfOutside(ServerPlayer player, Vec3 target){
        if(map.containsKey(player.getUUID()) && map.get(player.getUUID()).outsideEnough(target)){
            map.remove(player.getUUID());
            onChange(player);
        }
    }

    public static void onChange(ServerPlayer player){
        if(map.containsKey(player.getUUID())) syncBorder(player, map.get(player.getUUID()));
        else syncBorder(player, null);
    }

    public static Border getCurrentBorder(ServerPlayer player){
        if(map.containsKey(player.getUUID())) return map.get(player.getUUID());
        else return null;
    }

    public static void syncBorder(ServerPlayer player, Border border){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncBorderPacket(border, false));
    }

    public static void clearList(ServerPlayer player){
        map.remove(player.getUUID());
        onChange(player);
    }

    public static void renderBorder(PoseStack pPoseStack) {
        if(clientBorder == null) return;
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(pPoseStack.last().pose());

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        double maxX = clientBorder.maxX;
        double minX = clientBorder.minX;
        double maxZ = clientBorder.maxZ;
        double minZ = clientBorder.minZ;

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        double d0 = (double)(Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);
        if (!(camera.getPosition().x < maxX - d0) || !(camera.getPosition().x > minX + d0) || !(camera.getPosition().z < maxZ - d0) || !(camera.getPosition().z > minZ + d0)) {
            double d1 = 1.0D - Math.min(Math.min(Math.abs(camera.getPosition().x - 1), Math.abs(camera.getPosition().x + 1)), Math.min(Math.abs(camera.getPosition().z - 1), Math.abs(camera.getPosition().z + 1))) / d0;
            d1 = Math.pow(d1, 4.0D);
            d1 = Mth.clamp(d1, 0.0D, 1.0D);
            double d2 = camera.getPosition().x;
            double d3 = camera.getPosition().z;
            double d4 = (double)Minecraft.getInstance().gameRenderer.getDepthFar();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            posestack.pushPose();
            RenderSystem.applyModelViewMatrix();
            int i = 0x9900ff;
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            RenderSystem.setShaderColor(f, f1, f2, (float)d1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.polygonOffset(-3.0F, -3.0F);
            RenderSystem.enablePolygonOffset();
            RenderSystem.disableCull();
            float f3 = (float)(Util.getMillis() % 3000L) / 3000.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = (float)(d4 - Mth.frac(camera.getPosition().y));
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            double d5 = Math.max((double)Mth.floor(d3 - d0), minZ);
            double d6 = Math.min((double)Mth.ceil(d3 + d0), maxZ);
            if (d2 > maxX - d0) {
                float f7 = 0.0F;

                for(double d7 = d5; d7 < d6; f7 += 0.5F) {
                    double d8 = Math.min(1.0D, d6 - d7);
                    float f8 = (float)d8 * 0.5F;
                    bufferbuilder.vertex(maxX - d2, -d4, d7 - d3).uv(f3 - f7, f3 + f6).endVertex();
                    bufferbuilder.vertex(maxX - d2, -d4, d7 + d8 - d3).uv(f3 - (f8 + f7), f3 + f6).endVertex();
                    bufferbuilder.vertex(maxX - d2, d4, d7 + d8 - d3).uv(f3 - (f8 + f7), f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(maxX - d2, d4, d7 - d3).uv(f3 - f7, f3 + 0.0F).endVertex();
                    ++d7;
                }
            }

            if (d2 < minX + d0) {
                float f9 = 0.0F;

                for(double d9 = d5; d9 < d6; f9 += 0.5F) {
                    double d12 = Math.min(1.0D, d6 - d9);
                    float f12 = (float)d12 * 0.5F;
                    bufferbuilder.vertex(minX - d2, -d4, d9 - d3).uv(f3 + f9, f3 + f6).endVertex();
                    bufferbuilder.vertex(minX - d2, -d4, d9 + d12 - d3).uv(f3 + f12 + f9, f3 + f6).endVertex();
                    bufferbuilder.vertex(minX - d2, d4, d9 + d12 - d3).uv(f3 + f12 + f9, f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(minX - d2, d4, d9 - d3).uv(f3 + f9, f3 + 0.0F).endVertex();
                    ++d9;
                }
            }

            d5 = Math.max((double)Mth.floor(d2 - d0), minX);
            d6 = Math.min((double)Mth.ceil(d2 + d0), maxX);
            if (d3 > maxZ - d0) {
                float f10 = 0.0F;

                for(double d10 = d5; d10 < d6; f10 += 0.5F) {
                    double d13 = Math.min(1.0D, d6 - d10);
                    float f13 = (float)d13 * 0.5F;
                    bufferbuilder.vertex(d10 - d2, -d4, maxZ - d3).uv(f3 + f10, f3 + f6).endVertex();
                    bufferbuilder.vertex(d10 + d13 - d2, -d4, maxZ - d3).uv(f3 + f13 + f10, f3 + f6).endVertex();
                    bufferbuilder.vertex(d10 + d13 - d2, d4, maxZ - d3).uv(f3 + f13 + f10, f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(d10 - d2, d4, maxZ - d3).uv(f3 + f10, f3 + 0.0F).endVertex();
                    ++d10;
                }
            }

            if (d3 < minZ + d0) {
                float f11 = 0.0F;

                for(double d11 = d5; d11 < d6; f11 += 0.5F) {
                    double d14 = Math.min(1.0D, d6 - d11);
                    float f14 = (float)d14 * 0.5F;
                    bufferbuilder.vertex(d11 - d2, -d4, minZ - d3).uv(f3 - f11, f3 + f6).endVertex();
                    bufferbuilder.vertex(d11 + d14 - d2, -d4, minZ - d3).uv(f3 - (f14 + f11), f3 + f6).endVertex();
                    bufferbuilder.vertex(d11 + d14 - d2, d4, minZ - d3).uv(f3 - (f14 + f11), f3 + 0.0F).endVertex();
                    bufferbuilder.vertex(d11 - d2, d4, minZ - d3).uv(f3 - f11, f3 + 0.0F).endVertex();
                    ++d11;
                }
            }

            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            RenderSystem.enableCull();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.disableBlend();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.depthMask(true);

            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    // events
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        BorderManager.clearList(player);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if(player.level.isClientSide) {
            Border border = BorderManager.clientBorder;
            if(border != null){
                Vec3 pos = player.position();
                Vec3 delta = player.getDeltaMovement();
                double x = pos.x;
                double deltaX = delta.x;
                double z = pos.z;
                double deltaZ = delta.z;
                if(x < border.minX + 0.3D){
                    x = border.minX + 0.3D;
                    deltaX = 0;
                } else if(x > border.maxX - 0.3D){
                    x = border.maxX - 0.3D;
                    deltaX = 0;
                }
                if(z < border.minZ + 0.3D){
                    z = border.minZ + 0.3D;
                    deltaZ = 0;
                } else if(z > border.maxZ - 0.3D){
                    z = border.maxZ - 0.3D;
                    deltaZ = 0;
                }
                player.setPos(x, pos.y, z);
                player.setDeltaMovement(deltaX, delta.y, deltaZ);
            }
        } else {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            Border border = BorderManager.getCurrentBorder(serverPlayer);
            if(border != null && border.outsideEnough(serverPlayer.position())){
                double x = serverPlayer.getX();
                double z = serverPlayer.getZ();
                if(x <= border.minX - 1) x = border.minX + 0.3D;
                else if(x >= border.maxX + 1) x = border.maxX - 0.3D;
                if(z <= border.minZ - 1) z = border.minZ + 0.3D;
                else if(z >= border.maxZ + 1) z = border.maxZ - 0.3D;
                serverPlayer.teleportTo(x, serverPlayer.getY(), z);
            }
        }
    }

    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            BorderManager.clearList(player);
        }
    }

    public static void onEntityTeleport(EntityTeleportEvent.TeleportCommand event){
        if(event.getEntity() instanceof ServerPlayer player){
            BorderManager.removeIfOutside(player, event.getTarget());
            BorderManager.onChange(player);
        }
    }

    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        clientBorder = null;
    }

    public static void onClientRespawn(ClientPlayerNetworkEvent.RespawnEvent event){
        clientBorder = null;
    }

    public static void onRenderLevelLast(RenderLevelLastEvent event){
        renderBorder(event.getPoseStack());
    }
}
