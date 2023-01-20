package io.github.guy7cc.client.screen.party;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.client.screen.ScreenUtil;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundManagePartyPacket;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.rpg.PartyList;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenEvent;

import java.util.UUID;


public class PartyMenuScreen extends Screen {
    public static final ResourceLocation PARTY_MENU_BUTTON_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/party_menu_button.png");
    public static final ResourceLocation PARTY_MENU_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/party_menu.png");
    public static final ResourceLocation REFRESH_BUTTON_LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/party_menu_refresh_button.png");
    private PartySelectionList partySelectionList;
    private Button joinRequestButton;
    private Button createPartyButton;
    private Button changeLeaderButton;
    private Button leavePartyButton;
    private ImageButton refreshButton;
    private int leftPos;
    private int topPos;
    private int imageWidth = 100;
    private int imageHeight = 219;
    private int titleLabelX;
    private int titleLabelY;

    private PartyList partyList;
    private Player player;
    private Component partyName;

    public PartyMenuScreen() {
        super(new TranslatableComponent("gui.rpgwmod.partyMenu"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = this.width / 2 - 170;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.titleLabelX = leftPos + 5;
        this.titleLabelY = topPos + 5;

        this.partySelectionList = new PartySelectionList(this, this.minecraft, 218, this.height, 0, this.height, 50);
        this.joinRequestButton = new Button(this.leftPos + 10, this.topPos + 106, 80, 20, new TranslatableComponent("gui.rpgwmod.partyMenu.joinRequest"), button -> {
            PartySelectionList.PartyEntry entry = partySelectionList.getSelected();
            if(entry != null && !entry.getParty().isMember(this.minecraft.player.getUUID())){
                RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.JOIN_REQUEST, "", this.minecraft.player.getUUID(), entry.getParty().getId()));
            }
            else{
                this.minecraft.player.displayClientMessage(new TranslatableComponent("gui.rpgwmod.partyMenu.joinRequestFail"), false);
            }
        });
        this.createPartyButton = new Button(this.leftPos + 10, this.topPos + 128, 80, 20, new TranslatableComponent("gui.rpgwmod.createPartyMenu.create"), button -> {
            this.minecraft.getInstance().setScreen(new CreatePartyScreen());
        });
        this.changeLeaderButton = new Button(this.leftPos + 10, this.topPos + 150, 80, 20, new TranslatableComponent("gui.rpgwmod.partyMenu.changeLeader"), button -> {
            RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.CHANGE_LEADER, "", this.minecraft.player.getUUID(), 0));
        });
        this.leavePartyButton = new Button(this.leftPos + 10, this.topPos + 172, 80, 20, new TranslatableComponent("gui.rpgwmod.partyMenu.leave"), button -> {
            RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.LEAVE, "", this.minecraft.player.getUUID(), 0));
        });
        this.refreshButton = new ImageButton(this.leftPos + 40, this.topPos + 194, 20, 20, 0, 0, 20, REFRESH_BUTTON_LOCATION, 32, 64, button -> {
            RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.REQUEST_INFO, "", Util.NIL_UUID, 0));
        });
        addWidget(this.partySelectionList);
        addRenderableWidget(this.joinRequestButton);
        addRenderableWidget(this.createPartyButton);
        addRenderableWidget(this.changeLeaderButton);
        addRenderableWidget(this.leavePartyButton);
        addRenderableWidget(this.refreshButton);
        RpgwMessageManager.sendToServer(new ServerboundManagePartyPacket(ServerboundManagePartyPacket.Type.REQUEST_INFO, "", Util.NIL_UUID, 0));
        refresh();
    }

    public int getLeftPos() { return leftPos; }

    public int getTopPos() { return topPos; }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.partySelectionList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        this.renderStrings(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY){
        fill(pPoseStack, this.leftPos + 10, this.topPos + 20, this.leftPos + 90, this.topPos + 60, 0xff000000);
        InventoryScreen.renderEntityInInventory(this.leftPos + 50, this.topPos + 87, 30, (float)(this.leftPos + 50) - pX, (float)(this.topPos + 87 - 50) - pY, this.player);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, PARTY_MENU_LOCATION);
        this.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 128, 256);
    }

    private void renderStrings(PoseStack pPoseStack){pPoseStack.pushPose();pPoseStack.translate(0, 0, 5);
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0, 100);
        this.font.draw(pPoseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
        Component currentParty = new TranslatableComponent("gui.rpgwmod.partyMenu.currentParty").withStyle(Style.EMPTY.withUnderlined(true));
        this.font.draw(pPoseStack, currentParty, this.leftPos + 50 - this.font.width(currentParty) / 2f, this.topPos + 63, 4210752);
        this.font.draw(pPoseStack, this.partyName, this.leftPos + 50 - this.font.width(this.partyName) / 2f, this.topPos + 75, 4210752);
        pPoseStack.popPose();
    }

    public void setSelected(PartySelectionList.PartyEntry pSelected) {
        this.partySelectionList.setSelected(pSelected);
        this.refreshButtons();
    }

    public void refresh(){ this.refresh(null); }

    public void refresh(CompoundTag partyListTag){
        if(partyListTag != null) this.partyList = PartyList.deserializeNBT(partyListTag);
        this.partySelectionList.updatePartyEntryList();
        refreshButtons();
        if(this.partyList != null){
            Party party = this.partyList.byPlayer(this.getMinecraft().player.getUUID());
            if(party != null && party.getMemberList().size() > 0){
                AbstractClientPlayer leader = null;
                for(AbstractClientPlayer p : this.minecraft.level.players()){
                    if(p.getUUID().equals(party.getMemberList().get(0))) leader = p;
                }
                //this.player = new RemotePlayer(this.minecraft.level, this.minecraft.getConnection().getPlayerInfo(party.getMemberList().get(0)).getProfile());
                if(leader != null){
                    this.player = leader;
                } else{
                    this.player = this.minecraft.player;
                }
                this.partyName = new TextComponent(party.getName()).withStyle(Style.EMPTY.withBold(true));
            } else {
                this.player = this.minecraft.player;
                this.partyName = new TranslatableComponent("gui.rpgwmod.partyMenu.noCurrentParty");
            }
        } else {
            this.player = this.minecraft.player;
            this.partyName = new TranslatableComponent("gui.rpgwmod.partyMenu.noCurrentParty");
        }
    }

    public void refreshButtons(){
        if(this.partyList == null){
            this.joinRequestButton.active = false;
            this.createPartyButton.active = false;
            this.changeLeaderButton.active = false;
            this.leavePartyButton.active = false;
            this.refreshButton.active = true;
        } else {
            UUID uuid = Minecraft.getInstance().player.getUUID();
            PartySelectionList.PartyEntry entry = partySelectionList.getSelected();
            this.joinRequestButton.active = entry != null && partyList.canJoinParty(uuid, entry.getParty().getId());
            this.createPartyButton.active = partyList.canCreateParty(uuid);
            this.changeLeaderButton.active = partyList.canChangeLeader(uuid);
            this.leavePartyButton.active = partyList.canLeaveParty(uuid);
            this.refreshButton.active = true;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
        if(super.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        return ScreenUtil.closeIfInventoryKeyPressed(minecraft, pKeyCode, pScanCode);
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    public PartyList getPartyList() { return partyList; }

    public static void onInitScreenPre(ScreenEvent.InitScreenEvent.Post event){
        if(event.getScreen() instanceof InventoryScreen screen){
            event.addListener(new ImageButton(screen.getGuiLeft() + screen.getXSize() + 1, screen.getGuiTop() + screen.getYSize() - 20, 20, 20, 0, 0, 20, PARTY_MENU_BUTTON_LOCATION, 32, 64, button -> {
                Minecraft.getInstance().setScreen(new PartyMenuScreen());
            }));
        }
    }
}
