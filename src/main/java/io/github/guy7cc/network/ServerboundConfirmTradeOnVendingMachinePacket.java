package io.github.guy7cc.network;

import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.resource.TraderDataElement;
import io.github.guy7cc.save.cap.PlayerMoney;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class ServerboundConfirmTradeOnVendingMachinePacket {
    private BlockPos blockPos;
    private TraderDataElement.Type type;
    private int index;
    private int count;

    public ServerboundConfirmTradeOnVendingMachinePacket(BlockPos blockPos, TraderDataElement.Type type, int index, int count){
        this.blockPos = blockPos;
        this.type = type;
        this.index = index;
        this.count = count;
    }
    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(blockPos);
        buf.writeInt(type.id);
        buf.writeInt(index);
        buf.writeInt(count);
    }

    public ServerboundConfirmTradeOnVendingMachinePacket(FriendlyByteBuf buf){
        blockPos = buf.readBlockPos();
        type = TraderDataElement.Type.fromId(buf.readInt());
        index = buf.readInt();
        count = buf.readInt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player == null) return;
            if(count <= 0) return;
            ServerLevel level = player.getLevel();
            if(level.getBlockEntity(blockPos) instanceof VendingMachineBlockEntity be){
                TraderData data = be.getTraderData();
                data.confirmTrade(player, type.id, index, count);
                be.setChanged();
                RpgwMessageManager.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), new ClientboundSyncVendingMachinePacket(be));
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
