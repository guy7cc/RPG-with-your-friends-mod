package io.github.guy7cc.network;

import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerboundManagePartyPacket {
    public Type type;
    public String name;
    public UUID uuid;
    public int id;

    public ServerboundManagePartyPacket(Type type, String name, UUID uuid, int id){
        this.type = type;
        this.name = name;
        this.uuid = uuid;
        this.id = id;
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(type.id);
        buf.writeUtf(name);
        buf.writeUUID(uuid);
        buf.writeInt(id);
    }

    public ServerboundManagePartyPacket(FriendlyByteBuf buf) {
        this.type = Type.byId(buf.readInt());
        this.name = buf.readUtf();
        this.uuid = buf.readUUID();
        this.id = buf.readInt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ServerPlayer sender = ctx.get().getSender();
        if(sender == null) return;
        ctx.get().enqueueWork(() -> {
            switch(this.type){
                case JOIN_REQUEST:
                    if(PartyList.getInstance().canJoinParty(uuid, id)){
                        Party party = PartyList.getInstance().byId(id);
                        ServerPlayer leader = sender.getServer().getPlayerList().getPlayer(party.getMemberList().get(0));
                        leader.displayClientMessage(new TranslatableComponent("gui.rpgw.partyMenu.joinRequestCheck", sender.getName()), false);
                        leader.displayClientMessage(
                                new TextComponent("[")
                                        .append(new TranslatableComponent("gui.rpgw.partyMenu.joinRequestAccept"))
                                        .append(new TextComponent("]"))
                                        .withStyle(Style.EMPTY.withColor(0x00ff00).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jreq accept " + this.uuid + " " + this.id)))
                                        .append(
                                                new TextComponent(" ")
                                        )
                                        .append(
                                                new TextComponent("[")
                                                        .append(new TranslatableComponent("gui.rpgw.partyMenu.joinRequestDeny"))
                                                        .append(new TextComponent("]"))
                                                        .withStyle(Style.EMPTY.withColor(0xff0000).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/jreq deny " + this.uuid + " " + this.id)))
                                        )
                                , false);
                    } else {
                        sender.displayClientMessage(new TranslatableComponent("gui.rpgw.partyMenu.joinRequestFail"), false);
                    }
                    break;
                case CREATE:
                    if(PartyList.getInstance().canCreateParty(sender.getUUID())){
                        PartyList.getInstance().createParty(this.name, this.uuid);
                        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> sender), new ClientboundSyncPartyListPacket(PartyList.getInstance()));
                    } else {
                        sender.displayClientMessage(new TranslatableComponent("gui.rpgw.createPartyMenu.cannotCreate"), false);
                    }
                    break;
                case CHANGE_LEADER:
                    if(PartyList.getInstance().canChangeLeader(this.uuid)){
                        PartyList.getInstance().changeLeader(this.uuid);
                        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> sender), new ClientboundSyncPartyListPacket(PartyList.getInstance()));
                    } else {
                        sender.displayClientMessage(new TranslatableComponent("gui.rpgw.partyMenu.cannotChangeLeader"), false);
                    }
                    break;
                case LEAVE:
                    if(PartyList.getInstance().canLeaveParty(sender.getUUID())){
                        PartyList.getInstance().leaveParty(this.uuid);
                        RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> sender), new ClientboundSyncPartyListPacket(PartyList.getInstance()));
                    } else {
                        sender.displayClientMessage(new TranslatableComponent("gui.rpgw.partyMenu.cannotLeave"), false);
                    }
                    break;
                case REQUEST_INFO:
                    RpgwMessageManager.send(PacketDistributor.PLAYER.with(() -> sender), new ClientboundSyncPartyListPacket(PartyList.getInstance()));
                    break;
            }
            ctx.get().setPacketHandled(true);
        });
    }

    public enum Type{
        JOIN_REQUEST(0),
        CREATE(1),
        CHANGE_LEADER(2),
        LEAVE(3),
        REQUEST_INFO(4);

        public final int id;
        Type(int id){
            this.id = id;
        }

        public static Type byId(int id){
            for(Type type : values()){
                if(type.id == id) return type;
            }
            throw new IllegalArgumentException("Illegal id for ServerboundManagePartyPacket.Type");
        }
    }
}
