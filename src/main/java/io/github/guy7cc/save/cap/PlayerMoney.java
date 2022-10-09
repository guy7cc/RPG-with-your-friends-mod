package io.github.guy7cc.save.cap;

import io.github.guy7cc.network.ClientboundSyncPlayerMoneyPacket;
import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

public class PlayerMoney implements INBTSerializable<CompoundTag> {
    protected long money;
    protected ServerPlayer player;

    public PlayerMoney(ServerPlayer player){
        this(player, 0);
    }

    public PlayerMoney(ServerPlayer player, long money){
        this.player = player;
        this.money = money;
    }

    public long getMoney(){
        return this.money;
    }

    public void setMoney(long money){
        this.money = money;
        syncToClient();
    }

    public void syncToClient(){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPlayerMoneyPacket(this.money));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("PlayerMoney", money);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.money = nbt.contains("PlayerMoney", Tag.TAG_LONG) ? nbt.getLong("PlayerMoney") : 0;
    }
}
