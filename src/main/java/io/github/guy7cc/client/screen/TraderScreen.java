package io.github.guy7cc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.entity.VendingMachineBlockEntity;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.network.ServerboundConfirmTradeOnVendingMachinePacket;
import io.github.guy7cc.resource.TraderDataElement;
import io.github.guy7cc.resource.TraderData;
import io.github.guy7cc.rpg.ITrader;
import io.github.guy7cc.save.cap.PropertyType;
import io.github.guy7cc.save.cap.RpgPlayerProperty;
import io.github.guy7cc.sync.RpgPlayerPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class TraderScreen extends Screen {
    public static final int NODE_BOX_HEIGHT = 126;
    public static final int MAX_AMOUNT = 999;
    public static final ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/trader.png");

    private ITrader owner;

    private TraderDataElement selected;
    private List<? extends TraderDataElement>[] lists;

    private Button countDown;
    private Button countUp;
    private Button confirm;
    private EditBox amountBox;

    private int activeTab = 0;
    private int scrollHeight = 0;
    private int amount = 0;
    private int mainX;
    private int mainY;
    private int subX;
    private int subY;
    private int elementX;
    private int elementY;

    public TraderScreen(ITrader owner) {
        super(new TextComponent(""));
        this.owner = owner;
        TraderData data = owner.getTraderData();
        lists = new List[3];
        for(int i = 0; i < 3; i++){
            lists[i] = data.getList(i);
        }
        for(; activeTab < 3; activeTab++){
            if(!lists[activeTab].isEmpty()) break;
        }
        activeTab %= 3;
    }

    @Override
    protected void init() {
        mainX = (width - 230) / 2;
        mainY = (height - 168) / 2 - 15;
        subX = mainX + 130;
        subY = mainY + 105;
        elementX = mainX + 8;
        elementY = mainY + 23;
        countDown = new Button(subX + 7, subY + 7, 15, 20, new TextComponent("<"), button -> {
            setAmount(amount <= 0 ? 0 : amount - 1);
            countDown.active = amount > 0;
            countUp.active = amount < getMaxAmount();
        });
        countDown.active = amount > 0;
        countUp = new Button(subX + 78, subY + 7, 15, 20, new TextComponent(">"), button -> {
            int max = getMaxAmount();
            setAmount(amount >= max ? max : amount + 1);
            countDown.active = amount > 0;
            countUp.active = amount < max;
        });
        countUp.active = amount < getMaxAmount();

        confirm = new Button(subX + 20, subY + 51, 60, 20, new TranslatableComponent("gui.rpgwmod.trader.confirm"), button -> confirm(button));

        amountBox = new EditBox(font, subX + 24, subY + 7, 52, 20, null);
        amountBox.setResponder(str -> {
            try{
                int num = Integer.parseInt(str);
                int max = Math.min(getMaxAmount(), MAX_AMOUNT);
                confirm.active = 0 < num && num <= max;
            } catch(NumberFormatException exception){
                confirm.active = false;
            }
        });
        amountBox.setValue(String.valueOf(amount));

        addRenderableWidget(countDown);
        addRenderableWidget(countUp);
        addRenderableWidget(confirm);
        addRenderableWidget(amountBox);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        //sub window
        setTexture();
        blit(pPoseStack, subX, subY, 130, 0, 100, 78, 256, 256);
        Component component;
        if(selected != null){
            String s = "";
            if(activeTab == 0) s = amount * ((TraderDataElement.Buy) selected).getPrice() + " " + RpgwMod.CURRENCY;
            else if(activeTab == 1) s = String.valueOf(amount * ((TraderDataElement.Sell) selected).getItemStack().getCount());
            else if(activeTab == 2) s = String.valueOf(amount * ((TraderDataElement.Barter) selected).getRequirement().getCount());
            component = new TranslatableComponent("gui.rpgwmod.trader.needs", s);
            font.draw(pPoseStack, component, subX + 8, subY + 35, 0x404040);
        }



        //widgets
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        //main window
        setTexture();
        blit(pPoseStack, mainX + 8, mainY + 23, 8, 8, 114, NODE_BOX_HEIGHT, 256, 256);

        renderElements(pPoseStack, pMouseX, pMouseY);

        setTexture();
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0, 160);

        String[] keys = new String[]{ "gui.rpgwmod.trader.buy", "gui.rpgwmod.trader.sell", "gui.rpgwmod.trader.barter"};
        for(int i = 0; i < 3; i++){
            if(i == activeTab) continue;
            blit(pPoseStack, mainX + i * 43, (height - 168) / 2 - 15, i * 43, 168 + (lists[i].isEmpty() ? 19 : 0), 44, 19, 256, 256);
            component = new TranslatableComponent(keys[i]);
            font.draw(pPoseStack, component.getString(), mainX + i * 43 + 22 - font.width(component) / 2, mainY + 5, lists[i].isEmpty() ? 0xffffff : 0x404040);
            setTexture();
        }
        blit(pPoseStack, mainX, mainY + 15, 0, 0, 8, 168, 256, 256);
        blit(pPoseStack, mainX + 8, mainY + 15, 8, 0, 114, 8, 256, 256);
        blit(pPoseStack, mainX + 8, mainY + 23 + NODE_BOX_HEIGHT, 8, 8 + NODE_BOX_HEIGHT, 114, 160 - NODE_BOX_HEIGHT, 256, 256);
        blit(pPoseStack, mainX + 122, mainY + 15, 122, 0, 8, 168, 256, 256);
        blit(pPoseStack, mainX + activeTab * 43, mainY, activeTab * 43, 168, 44, 19, 256, 256);
        if(selected != null){
            selected.render(itemRenderer, pPoseStack, mainX + 8, mainY + 153);
        }
        component = new TranslatableComponent(keys[activeTab]);
        font.draw(pPoseStack, component.getString(), mainX + activeTab * 43 + 22 - font.width(component) / 2, mainY + 5, lists[activeTab].isEmpty() ? 0xffffff : 0x404040);

        pPoseStack.popPose();

        renderTooltipForElements(pPoseStack, pMouseX, pMouseY);
    }

    private void renderElements(PoseStack poseStack, int mouseX, int mouseY){
        List<? extends TraderDataElement> list = lists[activeTab];
        int i = scrollHeight / TraderDataElement.HEIGHT;
        int y = -(scrollHeight % TraderDataElement.HEIGHT);
        for(; i < list.size() && y < NODE_BOX_HEIGHT; i++){
            TraderDataElement e = list.get(i);
            e.render(itemRenderer, poseStack, elementX, elementY + y);
            y += TraderDataElement.HEIGHT;
        }
    }

    private void renderTooltipForElements(PoseStack poseStack, int mouseX, int mouseY){
        List<? extends TraderDataElement> list = lists[activeTab];
        int i = scrollHeight / TraderDataElement.HEIGHT;
        int y = -(scrollHeight % TraderDataElement.HEIGHT);
        for(; i < list.size() && y < NODE_BOX_HEIGHT; i++){
            TraderDataElement e = list.get(i);
            if(elementX + 3 <= mouseX && mouseX <= elementX + 19 && elementY + y + 3 <= mouseY && mouseY <= elementY + y + 19){
                renderTooltip(poseStack, e.getItemStack(), mouseX, mouseY);
            }
            y += TraderDataElement.HEIGHT;
        }
        if(selected != null && elementX + 3 <= mouseX && mouseX <= elementX + 19 && elementY + 133 <= mouseY && mouseY <= elementY + 149){
            renderTooltip(poseStack, selected.getItemStack(), mouseX, mouseY);
        }
        if(activeTab == 2){
            i = scrollHeight / TraderDataElement.HEIGHT;
            y = -(scrollHeight % TraderDataElement.HEIGHT);
            List<TraderDataElement.Barter> barterList = list.stream().map(e -> (TraderDataElement.Barter)e).collect(Collectors.toList());
            for(; i < list.size() && y < NODE_BOX_HEIGHT; i++){
                TraderDataElement.Barter e = barterList.get(i);
                if(elementX + 95 <= mouseX && mouseX <= elementX + 111 && elementY + y + 3 <= mouseY && mouseY <= elementY + y + 19){
                    renderTooltip(poseStack, e.getRequirement(), mouseX, mouseY);
                }
                y += TraderDataElement.HEIGHT;
            }
            if(selected != null && elementX + 95 <= mouseX && mouseX <= elementX + 111 && elementY + 133 <= mouseY && mouseY <= elementY + 149){
                renderTooltip(poseStack, ((TraderDataElement.Barter)selected).getRequirement(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void tick() {
        amountBox.tick();
        try{
            if(amountBox.getValue().equals("")) amount = 0;
            else{
                int max = Math.min(getMaxAmount(), MAX_AMOUNT);
                int num = Math.min(max, Math.max(0, Integer.parseInt(amountBox.getValue())));
                setAmount(num);
            }
        } catch(NumberFormatException exception){
            amountBox.setValue(String.valueOf(amount));
        }
    }

    private void setTexture(){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, LOCATION);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
        if(super.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        return ScreenUtil.closeIfInventoryKeyPressed(minecraft, pKeyCode, pScanCode);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean clickSound = false;
        //tabs
        if(mainX < pMouseX && pMouseX < mainX + 130 && mainY <= pMouseY && pMouseY < mainY + 18){
            for(int i = 0; i < 3; i++){
                if(mainX + 43 * i <= pMouseX && pMouseX < mainX + 43 * (i + 1) && !lists[i].isEmpty()){
                    activeTab = i;
                    scrollHeight = 0;
                    selected = null;
                    setAmount(0);
                    countDown.active = amount > 0;
                    countUp.active = amount < getMaxAmount();
                    clickSound = true;
                    break;
                }
            }
        }
        //elements
        if(elementX < pMouseX && pMouseX < elementX + 114 && elementY < pMouseY && pMouseY < elementY + NODE_BOX_HEIGHT){
            int y = (int)pMouseY - elementY + scrollHeight;
            y /= TraderDataElement.HEIGHT;
            if(0 <= y && y < lists[activeTab].size()){
                TraderDataElement e = lists[activeTab].get(y);
                if(!(e instanceof TraderDataElement.Sell s) || System.currentTimeMillis() > s.getAvailableFrom()){
                    if(selected != e){
                        selected = e;
                        setAmount(0);
                        countDown.active = amount > 0;
                        countUp.active = amount < getMaxAmount();
                    }
                    clickSound = true;
                }
            }
        }

        if(clickSound){
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(elementX < pMouseX && pMouseX < elementX + 114 && elementY < pMouseY && pMouseY < elementY + NODE_BOX_HEIGHT){
            int maxScrollHeight = Math.max(lists[activeTab].size() * TraderDataElement.HEIGHT - NODE_BOX_HEIGHT, 0);
            scrollHeight -= pDelta * 3;
            if(scrollHeight < 0) scrollHeight = 0;
            else if(scrollHeight > maxScrollHeight) scrollHeight = maxScrollHeight;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    public void updateGui(TraderData data){
        lists = new List[3];
        for(int i = 0; i < 3; i++){
            lists[i] = data.getList(i);
        }
    }

    private void setAmount(int q){
        amount = q;
        amountBox.setValue(String.valueOf(q));
    }

    private int getMaxAmount(){
        RpgPlayerProperty p = RpgPlayerPropertyManager.get(Minecraft.getInstance().player.getUUID());
        if(selected instanceof TraderDataElement.Buy buy){
            return (int)Math.min(MAX_AMOUNT, Math.max(0, (p != null ? p.getValue(PropertyType.MONEY) : 0) / buy.getPrice()));
        } else if(selected instanceof TraderDataElement.Sell sell){
            return Math.min(MAX_AMOUNT, Math.max(0, Math.min(sell.getCount(), getAmountInInventory(sell.getItemStack()) / sell.getItemStack().getCount())));
        } else if(selected instanceof TraderDataElement.Barter barter){
            return Math.min(MAX_AMOUNT, Math.max(0, getAmountInInventory(barter.getRequirement()) / barter.getRequirement().getCount()));
        }
        return 0;
    }

    private int getAmountInInventory(ItemStack itemstack){
        int a = 0;
        Inventory inv = minecraft.player.getInventory();
        for(int i = 0; i < 41; i++){
            ItemStack is = inv.getItem(i);
            if(is.sameItem(itemstack)){
                a += is.getCount();
            }
        }
        return a;
    }

    private void confirm(Button button){
        if(amountBox.getValue().equals("")) {
            amountBox.setValue("0");
            return;
        }
        try{
            int num = Integer.parseInt(amountBox.getValue());
            if(num <= 0 || num > MAX_AMOUNT) return;
            if(owner instanceof VendingMachineBlockEntity vm){
                RpgwMessageManager.sendToServer(
                        new ServerboundConfirmTradeOnVendingMachinePacket(
                                vm.getBlockPos(),
                                TraderDataElement.Type.fromId(activeTab),
                                lists[activeTab].indexOf(selected),
                                amount
                        )
                );
                selected = null;
                setAmount(0);
            }

        } catch(NumberFormatException exception){
            return;
        }
    }
}
