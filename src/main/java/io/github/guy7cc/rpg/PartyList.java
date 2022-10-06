package io.github.guy7cc.rpg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.function.Consumer;

public class PartyList {
    private static PartyList instance;
    private static boolean initedOnce = false;
    private MinecraftServer server;
    private List<Party> partyList = new ArrayList<>();
    private static int id = 0;

    public final boolean isClientSide;

    //server-side constructor
    private PartyList(MinecraftServer server){
        this.server = server;
        this.isClientSide = false;
    }

    //client-side constructor
    private PartyList(List<Party> partyList) {
        this.partyList = partyList;
        this.isClientSide = true;
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

    public MinecraftServer getServer() { return server; }

    public boolean canJoinParty(UUID uuid, int id){
        if(isInParty(uuid)) return false;
        Party party = byId(id);
        if(party == null) return false;
        return true;
    }

    public boolean joinParty(UUID uuid, int id){
        if(isInParty(uuid)) return false;
        Party party = byId(id);
        if(party == null) return false;
        party.addMember(uuid);
        return true;
    }

    public boolean canCreateParty(UUID leaderUUID){
        return !isInParty(leaderUUID);
    }

    public boolean createParty(String name, UUID leaderUUID){
        if(isInParty(leaderUUID)) return false;
        ServerPlayer leader = server.getPlayerList().getPlayer(leaderUUID);
        if (leader != null) {
            partyList.add(new Party(name, leader, id()));
        }
        return true;
    }

    public boolean canLeaveParty(UUID memberUUID){
        Party party = getParty(memberUUID);
        return party != null && !party.isBound(memberUUID);
    }

    public boolean leaveParty(UUID memberUUID){
        Party party = getParty(memberUUID);
        if(party == null || party.isBound(memberUUID)) return false;
        party.removeMember(memberUUID);
        if(party.size() == 0) partyList.remove(party);
        return true;
    }

    public boolean forceLeaveParty(UUID memberUUID){
        Party party = getParty(memberUUID);
        if(party == null) return false;
        party.forceRemoveMember(memberUUID);
        return true;
    }

    public boolean canChangeLeader(UUID leaderUUID){
        Party party = getParty(leaderUUID);
        return party != null && party.isLeader(leaderUUID) && party.size() > 1;
    }

    public boolean changeLeader(UUID leaderUUID){
        Party party = getParty(leaderUUID);
        if(party == null || !party.isLeader(leaderUUID) || party.size() <= 1) return false;
        party.changeLeaderToNext();
        return true;
    }

    public boolean isInParty(UUID memberUUID){
        for(Party party : partyList){
            if(party.isMember(memberUUID)) return true;
        }
        return false;
    }

    public Party getParty(UUID memberUUID){
        for(Party party : partyList){
            if(party.isMember(memberUUID)) return party;
        }
        return null;
    }

    public Party byId(int id){
        for(Party party : partyList){
            if(party.getId() == id) return party;
        }
        return null;
    }

    public Party byLeader(UUID uuid){
        for(Party party : partyList){
            List<UUID> list = party.getMemberList();
            if(list.size() > 0 && list.get(0).equals(uuid)){
                return party;
            }
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
