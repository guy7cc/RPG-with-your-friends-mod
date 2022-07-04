package io.github.guy7cc.client.screen.party;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class PartySelectionList extends ObjectSelectionList<PartySelectionList.PartyEntry> {
    private final PartyMenuScreen screen;
    private final List<PartyEntry> partyEntryList = new ArrayList<>();

    public PartySelectionList(PartyMenuScreen screen, Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
        this.screen = screen;
        setLeftPos(this.screen.getLeftPos() + 110);
    }


    public void updatePartyEntryList(){
        this.partyEntryList.clear();
        PartyList partyList = this.screen.getPartyList();
        if(partyList != null){
            partyList.forEach(party -> {
                this.partyEntryList.add(new PartyEntry(this.screen, party));
            });
        }
        this.clearEntries();
        this.partyEntryList.forEach(entry -> {
            this.addEntry(entry);
        });
    }

    @Override
    protected int getScrollbarPosition() {
        return this.screen.getLeftPos() + 329;
    }

    @Override
    public int getRowWidth(){
        return 214;
    }

    @Override
    protected boolean isFocused(){
        return this.screen.getFocused() == this;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PartyEntry extends ObjectSelectionList.Entry<PartySelectionList.PartyEntry>{
        private PartyMenuScreen screen;
        private Party party;

        public PartyEntry(PartyMenuScreen screen, Party party) {
            this.screen = screen;
            this.party = party;
        }

        @Override
        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            drawString(pPoseStack, screen.getMinecraft().font, party.getName() + " (" + party.size() + ")", pLeft + 3, pTop + 3, 16777215);
            Minecraft minecraft = this.screen.getMinecraft();
            ClientPacketListener connection = minecraft.getConnection();
            List<PlayerInfo> list = party.getMemberList().stream().map(uuid -> connection.getPlayerInfo(uuid)).toList();
            boolean flag = minecraft.isLocalServer() || connection.getConnection().isEncrypted();
            if(flag){
                int pos = pLeft - 1;
                for(PlayerInfo info : list){
                    pos += 11;
                    if(info == null) continue;
                    RenderSystem.setShaderTexture(0, info.getSkinLocation());
                    GuiComponent.blit(pPoseStack, pos, pTop + 16, 8, 8, 8.0F, 8, 8, 8, 64, 64);
                    GuiComponent.blit(pPoseStack, pos, pTop + 16, 8, 8, 40.0F, 8, 8, 8, 64, 64);
                }
            }
            else{
                int pos = pLeft + 10;
                for(PlayerInfo info : list){
                    if(info == null) continue;
                    minecraft.font.draw(pPoseStack, info.getProfile().getName(), pos, pTop + 16, 16777215);
                    pos += minecraft.font.width(info.getProfile().getName()) + 3;
                }
            }
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
            this.screen.setSelected(this);
            return false;
        }

        @Override
        public Component getNarration() {
            return TextComponent.EMPTY;
        }

        public Party getParty() { return this.party; }

    }
}
