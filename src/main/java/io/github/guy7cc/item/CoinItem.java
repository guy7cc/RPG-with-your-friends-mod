package io.github.guy7cc.item;

import io.github.guy7cc.save.cap.PlayerMoney;
import io.github.guy7cc.syncdata.PlayerMoneyManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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

    public Rank getRank(){
        return rank;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            PlayerMoney playerMoney = PlayerMoneyManager.getPlayerMoneyCap((ServerPlayer) pPlayer);
            playerMoney.addMoney(rank.value);
            itemstack.shrink(1);
        }
        pPlayer.playSound(SoundEvents.ARMOR_EQUIP_NETHERITE, 1f,  3f);
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
    }

    public enum Rank{
        IRON(1),
        COPPER(100),
        SILVER(1000),
        GOLD(10000);

        public final int value;

        Rank(int value){
            this.value = value;
        }
    }
}
