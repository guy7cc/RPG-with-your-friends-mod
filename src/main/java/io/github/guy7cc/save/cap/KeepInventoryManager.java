package io.github.guy7cc.save.cap;

import io.github.guy7cc.save.cap.PlayerMiscCap;
import io.github.guy7cc.save.cap.PlayerMiscCapabilityProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.stream.Collectors;

public class KeepInventoryManager {
    private static Map<UUID, Boolean> keepInventoryMap = new HashMap<>();

    public static void addOrModifyPlayer(ServerPlayer player, boolean keepInventory){
        keepInventoryMap.put(player.getUUID(), keepInventory);
    }

    public static void removePlayerIfPresent(ServerPlayer player){
        keepInventoryMap.remove(player);
    }

    public static boolean keepInventory(ServerPlayer player){
        return keepInventoryMap.containsKey(player.getUUID()) && keepInventoryMap.get(player.getUUID());
    }

    public static void collectItems(ServerPlayer player){
        PlayerMiscCap cap = player.getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).orElse(null);
        if(cap != null){
            Inventory inv = player.getInventory();
            cap.keepInventory = new ArrayList<>();
            cap.keepInventory.addAll(inv.items);
            cap.keepInventory.addAll(inv.armor);
            cap.keepInventory.addAll(inv.offhand);
        }
    }

    public static void restoreInventory(ServerPlayer player){
        PlayerMiscCap cap = player.getCapability(PlayerMiscCapabilityProvider.PLAYER_MISC_CAPABILITY).orElse(null);
        if(cap != null){
            Inventory inv = player.getInventory();
            for(int i = 0; i < 36 && i < cap.keepInventory.size(); i++){
                inv.items.set(i, cap.keepInventory.get(i));
            }
            for(int i = 36; i < 40 && i < cap.keepInventory.size(); i++){
                inv.armor.set(i - 36, cap.keepInventory.get(i));
            }
            inv.offhand.set(0, cap.keepInventory.size() >= 41 ? cap.keepInventory.get(40) : ItemStack.EMPTY);
            inv.setChanged();
            cap.keepInventory.clear();
        }
    }
}
