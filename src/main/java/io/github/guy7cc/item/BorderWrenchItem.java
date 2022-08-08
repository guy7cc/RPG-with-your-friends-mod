package io.github.guy7cc.item;

import io.github.guy7cc.block.entity.IBorderBlockEntity;
import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.network.ClientboundSyncBorderPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class BorderWrenchItem extends Item {
    public BorderWrenchItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        Player player = pContext.getPlayer();
        if(entity instanceof IBorderBlockEntity borderBE){
            if(level.isClientSide){
                Minecraft.getInstance().setScreen(new BorderBlockEditScreen(blockstate.getBlock().getName(), entity));
                return InteractionResult.SUCCESS;
            } else {
                RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundSyncBorderPacket(borderBE.getBorder(), true));
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
