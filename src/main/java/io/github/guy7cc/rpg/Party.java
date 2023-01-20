package io.github.guy7cc.rpg;

import io.github.guy7cc.network.ClientboundSyncPartyPacket;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.syncdata.PlayerMpManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class Party {
    //the first element of memberList is the party's leader
    private String name;
    private int id;
    private List<UUID> memberList = new ArrayList<>();
    private MinecraftServer server;

    //server-side constructor
    public Party(String name, ServerPlayer leader, int id) {
        this.name = name;
        this.id = id;
        this.memberList.add(leader.getUUID());
        this.server = leader.getServer();
        onChange();
    }

    //client-side constructor
    private Party(String name, List<UUID> memberList, int id) {
        this.name = name;
        this.id = id;
        this.memberList.addAll(memberList);
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMemberList() {
        return Collections.unmodifiableList(memberList);
    }

    public List<ServerPlayer> getPlayers(){
        if(!isClientSide()){
            PlayerList playerList = server.getPlayerList();
            List<ServerPlayer> players = new ArrayList<>(size());
            for(UUID uuid : memberList){
                ServerPlayer p = playerList.getPlayer(uuid);
                if(p != null) players.add(p);
            }
            return players;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public int size() {
        return memberList.size();
    }

    public boolean isClientSide(){
        return server == null;
    }

    public MinecraftServer getServer(){
        return server;
    }

    public void addMember(UUID member) {
        if (!memberList.contains(member)) {
            memberList.add(member);
            onChange();
        }
    }

    public boolean removeMember(UUID member) {
        if(!isClientSide()){
            ServerPlayer player = server.getPlayerList().getPlayer(member);
            broadcastMessage(new TranslatableComponent(  "gui.rpgwmod.partyMenu.someoneLeave", player.getName()));
        }
        onRemoved(member);
        memberList.remove(member);
        onChange();
        return true;
    }

    public void onChange(){
        if(isClientSide()) return;
        for(UUID uuid : memberList){
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if(player != null){
                syncPartyToMember(player);
                PlayerMpManager.syncToParty(player);
            }
        }
    }

    public void onRemoved(UUID uuid){
        if(isClientSide()) return;
        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if(player != null) {
            RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPartyPacket((Party) null));
        }
    }

    public boolean isLeader(UUID leader) {
        return memberList.size() > 0 && memberList.get(0).equals(leader);
    }

    public boolean isMember(UUID member) {
        return memberList.contains(member);
    }

    public void changeLeaderToNext() {
        UUID leaderUUID = memberList.remove(0);
        memberList.add(leaderUUID);
    }

    public void broadcastMessage(Component component) {
        if(isClientSide()) return;
        PlayerList playerList = this.server.getPlayerList();
        for (UUID memberUUID : memberList) {
            ServerPlayer player = playerList.getPlayer(memberUUID);
            if (player != null) player.displayClientMessage(component, false);
        }
    }

    public void syncPartyToMember(ServerPlayer player){
        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSyncPartyPacket(this));
    }

    public CompoundTag serializeNBT() {
        CompoundTag partyTag = new CompoundTag();
        partyTag.putString("partyName", this.name);
        partyTag.putInt("partyId", this.id);
        CompoundTag memberTag = new CompoundTag();
        int i = 0;
        for (UUID memberUUID : memberList) {
            memberTag.putUUID("" + i++, memberUUID);
        }
        partyTag.put("memberList", memberTag);
        return partyTag;
    }

    public static Party deserializeNBT(CompoundTag tag) {
        String partyName = tag.getString("partyName");
        int partyId = tag.getInt("partyId");
        CompoundTag memberTag = tag.getCompound("memberList");
        List<UUID> memberList = new ArrayList<>();
        for (String key : memberTag.getAllKeys()) {
            memberList.add(memberTag.getUUID(key));
        }
        return new Party(partyName, memberList, partyId);
    }
}
