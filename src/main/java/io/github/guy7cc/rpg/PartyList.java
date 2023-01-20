package io.github.guy7cc.rpg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class PartyList {
    private static PartyList instance;
    private static boolean initedOnce = false;

    private MinecraftServer server;
    private List<Party> partyList = new ArrayList<>();
    private Map<UUID, Party> partyByPlayer = new HashMap<>();
    private Map<Integer, Party> partyById = new HashMap();

    private static int id = 0;

    //server-side constructor
    private PartyList(MinecraftServer server){
        this.server = server;
    }

    //client-side constructor
    private PartyList(List<Party> partyList) {
        this.partyList = partyList;
    }

    public static void init(MinecraftServer server){
        if(!initedOnce){
            instance = new PartyList(server);
            initedOnce = true;
        }
    }

    public static boolean initedOnce(){ return initedOnce; }

    public static PartyList getInstance() {
        if(!initedOnce) throw new IllegalArgumentException("PartyList is not initialized yet.");
        return instance;
    }

    public boolean isClientSide(){
        return server == null;
    }

    public MinecraftServer getServer() { return server; }

    public boolean canJoinParty(@NotNull UUID uuid, int id){
        if(inParty(uuid)) return false;
        Party party = byId(id);
        if(party == null) return false;
        return true;
    }

    public boolean joinParty(@NotNull UUID uuid, int id){
        if(inParty(uuid)) return false;
        Party party = byId(id);
        if(party == null) return false;
        party.addMember(uuid);
        partyByPlayer.put(uuid, party);
        return true;
    }

    public boolean canCreateParty(@NotNull UUID leaderUUID){
        return !inParty(leaderUUID);
    }

    public boolean createParty(String name, @NotNull UUID leaderUUID){
        if(inParty(leaderUUID)) return false;
        ServerPlayer leader = server.getPlayerList().getPlayer(leaderUUID);
        if (leader != null) {
            Party party = new Party(name, leader, id());
            partyList.add(party);
            partyByPlayer.put(leaderUUID, party);
            partyById.put(party.getId(), party);
        }
        return true;
    }

    public boolean canLeaveParty(@NotNull UUID memberUUID){
        return inParty(memberUUID);
    }

    public boolean leaveParty(@NotNull UUID memberUUID){
        Party party = byPlayer(memberUUID);
        if(party == null) return false;
        party.removeMember(memberUUID);
        partyByPlayer.remove(memberUUID);
        if(party.size() == 0) {
            partyList.remove(party);
            partyById.remove(party.getId());
        }
        return true;
    }

    public boolean canChangeLeader(@NotNull UUID leaderUUID){
        Party party = byPlayer(leaderUUID);
        return party != null && party.isLeader(leaderUUID) && party.size() > 1;
    }

    public boolean changeLeader(@NotNull UUID leaderUUID){
        Party party = byPlayer(leaderUUID);
        if(party == null || !party.isLeader(leaderUUID) || party.size() <= 1) return false;
        party.changeLeaderToNext();
        return true;
    }

    public boolean inParty(@NotNull UUID memberUUID){
        return partyByPlayer.get(memberUUID) != null;
    }

    public Party byPlayer(@NotNull UUID memberUUID){
        return partyByPlayer.get(memberUUID);
    }

    public Party byId(int id){
        return partyById.get(id);
    }

    public Party byLeader(@NotNull UUID leaderUUID){
        Party party = partyByPlayer.get(leaderUUID);
        if(party != null){
            List<UUID> list = party.getMemberList();
            return list.size() > 0 && list.get(0).equals(leaderUUID) ? party : null;
        }
        return null;
    }

    public void forEach(Consumer<Party> consumer){
        for(Party party : partyList){
            consumer.accept(party);
        }
    }

    private static int id() { return id++; }

    public CompoundTag serializeNBT(){
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for(Party party : partyList){
            tag.put("" + i++, party.serializeNBT());
        }
        return tag;
    }

    public static PartyList deserializeNBT(CompoundTag tag){
        List<Party> partyList = new ArrayList<>();
        for(String key : tag.getAllKeys()){
            CompoundTag partyTag = tag.getCompound(key);
            partyList.add(Party.deserializeNBT(partyTag));
        }
        return new PartyList(partyList);
    }

}
