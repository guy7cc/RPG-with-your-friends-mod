package io.github.guy7cc.rpg;

import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.rpg.Party;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class PartyManager {
    public static Party clientParty;

    public static void refresh(CompoundTag partyTag){
        if(partyTag != null){
            clientParty = Party.deserializeNBT(partyTag);
        } else {
            clientParty = null;
        }
        RpgwIngameOverlay.refreshPartyStatus();
    }

    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        clientParty = null;
    }
}
