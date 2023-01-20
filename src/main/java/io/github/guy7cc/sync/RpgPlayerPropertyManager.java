package io.github.guy7cc.sync;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.network.ClientboundSetRpgPlayerPropertyPacket;
import io.github.guy7cc.network.ClientboundSyncPlayerMpPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.save.cap.RpgPlayerPropertyProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RpgPlayerPropertyManager {
    public static final int UPDATE_INTERVAL = 2;

    // Player UUID to property for client-side
    private static final Map<UUID, RpgPlayerProperty> map = new HashMap<>();

    // Client-side functions
    public static void update(UUID uuid, RpgPlayerProperty property){
        map.put(uuid, property);
    }

    public static void clear(){
        map.clear();
    }

    // Server-side functions
    public static RpgPlayerProperty get(ServerPlayer player){
        return player.getCapability(RpgPlayerPropertyProvider.RPG_PLAYER_PROPERTY_CAPABILITY).orElse(null);
    }

    public static void syncToClient(ServerPlayer player, RpgPlayerProperty p){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSetRpgPlayerPropertyPacket(player, p));
    }

    public static void syncToParty(ServerPlayer player, RpgPlayerProperty p){
        Party party = PartyList.getInstance().byPlayer(player.getUUID());
        if(party == null) return;
        MinecraftServer server = player.getServer();
        for(UUID uuid : party.getMemberList()){
            ServerPlayer member = server.getPlayerList().getPlayer(uuid);
            if(member != null && !uuid.equals(player.getUUID())){
                RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> member), new ClientboundSetRpgPlayerPropertyPacket(player, p));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        RpgPlayerProperty p = get(player);
        if(p != null) syncToClient(player, p);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player instanceof ServerPlayer player && player.tickCount % UPDATE_INTERVAL == 0){
            RpgPlayerProperty p = get(player);
            if(p == null || !p.isDirty()) return;
            syncToClient(player, p);
            syncToParty(player, p);
        }
    }
}
