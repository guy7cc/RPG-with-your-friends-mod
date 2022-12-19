package io.github.guy7cc.datagen.lang;

import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.item.RpgwItems;
import net.minecraft.data.DataGenerator;

public class RpgwEnUsLanguageProvider extends RpgwAbstractLanguageProvider{
    public RpgwEnUsLanguageProvider(DataGenerator gen, String modId) {
        super(gen, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItemGroup("rpgwmod", "RPG with your friends Mod");
        
        add(RpgwBlocks.EXAMPLE_BORDER.get(), "Example Border Block");
        add(RpgwBlocks.RPGW_SPAWNER.get(), "RPG Spawner");
        add(RpgwBlocks.BORDERED_RPGW_SPAWNER.get(), "Bordered RPG Spawner");
        add(RpgwBlocks.VENDING_MACHINE.get(), "Vending Machine");
        
        add(RpgwItems.BORDER_WRENCH.get(), "Wrench (Border)");
        add(RpgwItems.SPAWNER_WRENCH.get(), "Wrench (Spawner)");
        add(RpgwItems.IRON_COIN.get(), "Iron Coin");
        add(RpgwItems.COPPER_COIN.get(), "Copper Coin");
        add(RpgwItems.SILVER_COIN.get(), "Silver Coin");
        add(RpgwItems.GOLD_COIN.get(), "Gold Coin");
        
        String prefix = "partyMenu.";
        addGui("partyMenu", "PartyMenu");
        addGui(prefix + "currentParty", "Current Party");
        addGui(prefix + "noCurrentParty", "None");
        addGui(prefix + "joinRequest", "Join Request");
        addGui(prefix + "joinRequestCheck", "Received join request from %s.");
        addGui(prefix + "joinRequestAccept", "ACCEPT");
        addGui(prefix + "joinRequestDeny", "DENY");
        addGui(prefix + "joinRequestFail", "Failed to send a join request.");
        addGui(prefix + "changeLeader", "Change Leader");
        addGui(prefix + "cannotChangeLeader", "You are not a leader.");
        addGui(prefix + "leave", "Leave");
        addGui(prefix + "cannotLeave", "You cannot leave the party now.");
        addGui(prefix + "someoneLeave", "%s left the party.");
        
        prefix = "createPartyMenu.";
        addGui("createPartyMenu", "Party Name");
        addGui(prefix + "create", "Create");
        addGui(prefix + "cancel", "Cancel");
        addGui(prefix + "cannotCreate", "You have to leave the current party in order to create a new party.");
        
        prefix = "editScreen.";
        addGui(prefix + "min.x", "Minimum X");
        addGui(prefix + "min.y", "Minimum Y");
        addGui(prefix + "min.z", "Minimum Z");
        addGui(prefix + "max.x", "Maximum X");
        addGui(prefix + "max.y", "Maximum Y");
        addGui(prefix + "max.z", "Maximum Z");
        addGui(prefix + "invalid", "Cannot save invalid values.");
        addGui(prefix + "spawnArea", "Spawn");
        addGui(prefix + "playerArea", "Player");
        addGui(prefix + "delay", "Delay");
        
        prefix = "trader.";
        addGui(prefix + "buy", "Buy");
        addGui(prefix + "sell", "Sell");
        addGui(prefix + "barter", "Barter");
        addGui(prefix + "upto", "Up to %s");
        addGui(prefix + "available", "%s to go");
        addGui(prefix + "needs", "Needs %s");
        addGui(prefix + "confirm", "Confirm");

        String command = "rpgw";
        addCommands(command, "jreqAccepted", "The join request has been accepted.");
        addCommands(command, "jreqDenied", "The join request has been denied.");
        addCommands(command, "jreqFail", "Failed to process the join request.");
    }
}
