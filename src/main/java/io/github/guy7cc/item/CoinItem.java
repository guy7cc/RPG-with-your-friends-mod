package io.github.guy7cc.item;

import io.github.guy7cc.save.cap.PlayerMoney;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CoinItem extends Item {
    private Rank rank;

    public CoinItem(Rank rank, Properties pProperties) {
        super(pProperties);
        this.rank = rank;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            PlayerMoney playerMoney = PlayerMoneyManager.getPlayerMoneyCap((ServerPlayer) pPlayer);
            playerMoney.addMoney(rank.value);
            itemstack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
    }

    public enum Rank{
        COPPER(10),
        SILVER(100),
        GOLD(1000);

        public final int value;

        Rank(int value){
            this.value = value;
        }
    }
}
