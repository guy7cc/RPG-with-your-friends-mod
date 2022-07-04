package io.github.guy7cc.syncdata;

import io.github.guy7cc.client.overlay.RpgwIngameOverlay;
import io.github.guy7cc.rpg.Party;
import net.minecraft.nbt.CompoundTag;

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
}
