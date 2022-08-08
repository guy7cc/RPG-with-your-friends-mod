package io.github.guy7cc.block;

import io.github.guy7cc.block.entity.IBorderBlockEntity;
import io.github.guy7cc.client.screen.BorderBlockEditScreen;
import io.github.guy7cc.item.RpgwItems;
import io.github.guy7cc.network.ClientboundSyncBorderPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.syncdata.BorderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

public abstract class AbstractBorderBlock extends BaseEntityBlock {

    public AbstractBorderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(!pLevel.isClientSide && pState.getBlock() != pNewState.getBlock()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(entity instanceof IBorderBlockEntity borderBE){
                BorderManager.remove(serverLevel.getServer(), borderBE.getBorder().id);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    public static void onRemove(Block block, BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(!pLevel.isClientSide && pState.getBlock() != pNewState.getBlock()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(entity instanceof IBorderBlockEntity borderBE){
                BorderManager.remove(serverLevel.getServer(), borderBE.getBorder().id);
            }
        }
    }
}
