package io.github.guy7cc.rpg;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.*;

public class Party {
    //the first element of memberList is the party's leader
    private String name;
    private int id;
    private List<UUID> memberList = new ArrayList<>();
    private Set<UUID> bindList = new HashSet<>();
    //if the party was created on server, this value is not null
    private MinecraftServer server;

    //server-side constructor
    public Party(String name, ServerPlayer leader, int id) {
        this.name = name;
        this.id = id;
        this.memberList.add(leader.getUUID());
        this.server = leader.getServer();
    }

    //client-side constructor
    private Party(String name, List<UUID> memberList, List<UUID> bindList, int id) {
        this.name = name;
        this.id = id;
        this.memberList.addAll(memberList);
        this.bindList.addAll(bindList);
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMemberList() {
        return new ArrayList<>(memberList);
    }

    public int getId() {
        return this.id;
    }

    public int size() {
        return memberList.size();
    }

    public void addMember(UUID member) {
        if (!memberList.contains(member)) memberList.add(member);
    }

    public boolean removeMember(UUID member) {
        if (bindList.contains(member)) {
            return false;
        } else {
            broadcastMessage(null);
            memberList.remove(member);
            return true;
        }
    }
    public void forceRemoveMember(UUID member){
        broadcastMessage(null);
        memberList.remove(member);
    }

    public void bindAll() {
        bindList.addAll(memberList);
    }

    public boolean isLeader(UUID leader) {
        return memberList.size() > 0 && memberList.get(0).equals(leader);
    }

    public boolean isMember(UUID member) {
        return memberList.contains(member);
    }

    public boolean isBound(UUID member) {
        return bindList.contains(member);
    }

    public void changeLeaderToNext() {
        UUID leaderUUID = memberList.remove(0);
        memberList.add(leaderUUID);
    }

    public void broadcastMessage(Component component) {
        if(this.server == null) return;
        PlayerList playerList = this.server.getPlayerList();
        for (UUID memberUUID : memberList) {
            ServerPlayer player = playerList.getPlayer(memberUUID);
            if (player != null) player.displayClientMessage(component, false);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag partyTag = new CompoundTag();
        partyTag.putString("partyName", this.name);
        partyTag.putInt("partyId", this.id);
        CompoundTag memberTag = new CompoundTag();
        CompoundTag bindTag = new CompoundTag();
        int i = 0;
        for (UUID memberUUID : memberList) {
            memberTag.putUUID("" + i++, memberUUID);
        }
        i = 0;
        for (UUID bindUUID : bindList) {
            bindTag.putUUID("" + i++, bindUUID);
        }
        partyTag.put("memberList", memberTag);
        partyTag.put("bindList", bindTag);
        return partyTag;
    }

    public static Party deserializeNBT(CompoundTag tag) {
        String partyName = tag.getString("partyName");
        int partyId = tag.getInt("partyId");
        CompoundTag memberTag = tag.getCompound("memberList");
        CompoundTag bindTag = tag.getCompound("bindList");
        List<UUID> memberList = new ArrayList<>();
        List<UUID> bindList = new ArrayList<>();
        for (String key : memberTag.getAllKeys()) {
            memberList.add(memberTag.getUUID(key));
        }
        for (String key : bindTag.getAllKeys()) {
            bindList.add(bindTag.getUUID(key));
        }
        return new Party(partyName, memberList, bindList, partyId);
    }
}
