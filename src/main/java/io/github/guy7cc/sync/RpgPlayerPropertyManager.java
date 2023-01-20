package io.github.guy7cc.sync;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.network.ClientboundSetRpgPlayerPropertyPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RpgPlayerPropertyManager {
    public static final int UPDATE_INTERVAL = 2;

    // Player UUID to property for client-side
    private static final Map<UUID, RpgPlayerProperty> map = new HashMap<>();

    // Client-side functions
    public static RpgPlayerProperty get(UUID uuid){
        return map.get(uuid);
    }

    public static void update(UUID uuid, RpgPlayerProperty property){
        map.put(uuid, property);
        RpgwIngameOverlay.money.onChangeMoney();
    }

    public static void clear(){
        map.clear();
    }

    // Server-side functions
    public static RpgPlayerProperty get(ServerPlayer player){
        return player.getCapability(RpgPlayerPropertyProvider.RPG_PLAYER_PROPERTY_CAPABILITY).orElse(null);
    }

    public static void syncToClient(ServerPlayer player){
        RpgPlayerProperty property = get(player);
        if(property != null) RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSetRpgPlayerPropertyPacket(player, property));
    }

    public static void syncToParty(ServerPlayer player){
        RpgPlayerProperty property = get(player);
        Party party = PartyList.getInstance().byPlayer(player.getUUID());
        if(property != null || party == null) return;
        MinecraftServer server = player.getServer();
        for(UUID uuid : party.getMemberList()){
            ServerPlayer member = server.getPlayerList().getPlayer(uuid);
            if(member != null && !uuid.equals(player.getUUID())){
                RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> member), new ClientboundSetRpgPlayerPropertyPacket(player, property));
            }
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        RpgPlayerProperty p = get(player);
        if(p != null) syncToClient(player);
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        clear();
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player instanceof ServerPlayer player && player.tickCount % UPDATE_INTERVAL == 0){
            RpgPlayerProperty p = get(player);
            if(p == null || !p.isDirty()) return;
            syncToClient(player);
            syncToParty(player);
            p.clearDirty();
        }
    }

    public static void onLivingDrops(LivingDropsEvent event){
        if(event.getEntityLiving() instanceof ServerPlayer player){
            RpgPlayerProperty property = get(player);
            event.setCanceled(property != null ? property.isInventoryKept : false);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event){
        if(event.getEntityLiving() instanceof ServerPlayer player){
            RpgPlayerProperty property = get(player);
            if(property == null || !property.isInventoryKept) return;
            Inventory inv = player.getInventory();
            property.keptInventory.addAll(inv.items);
            property.keptInventory.addAll(inv.armor);
            property.keptInventory.addAll(inv.offhand);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        if(event.getPlayer() instanceof ServerPlayer player){
            RpgPlayerProperty property = get(player);
            if(property == null) return;
            Inventory inv = player.getInventory();
            for(int i = 0; i < 36 && i < property.keptInventory.size(); i++){
                inv.items.set(i, property.keptInventory.get(i));
            }
            for(int i = 36; i < 40 && i < property.keptInventory.size(); i++){
                inv.armor.set(i - 36, property.keptInventory.get(i));
            }
            inv.offhand.set(0, property.keptInventory.size() >= 41 ? property.keptInventory.get(40) : ItemStack.EMPTY);
            inv.setChanged();
            property.keptInventory.clear();
        }
    }
}
