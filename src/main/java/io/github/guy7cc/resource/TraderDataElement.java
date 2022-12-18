package io.github.guy7cc.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.RpgwMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public abstract class TraderDataElement implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int WIDTH = 114;
    public static final int HEIGHT = 22;
    public static final ResourceLocation LOCATION = new ResourceLocation(RpgwMod.MOD_ID, "textures/gui/trader.png");

    protected Minecraft minecraft;
    protected ItemStack itemStack;

    public TraderDataElement(ItemStack itemStack) {
        this.minecraft = Minecraft.getInstance();
        this.itemStack = itemStack;
    }

    public TraderDataElement(CompoundTag tag){
        this.minecraft = Minecraft.getInstance();
        deserializeNBT(tag);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void render(ItemRenderer itemRenderer, PoseStack poseStack, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, LOCATION);
        GuiComponent.blit(poseStack, x, y, 0, 206, WIDTH, HEIGHT, 256, 256);
        itemRenderer.renderAndDecorateItem(itemStack, x + 3, y + 3);
        itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, x + 3, y + 3, null);
    }

    public abstract TraderDataElement copy();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("ItemStack", this.itemStack.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        itemStack = ItemStack.of(nbt.getCompound("ItemStack"));
    }


    public static class Buy extends TraderDataElement {
        public static final Codec<Buy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Buy::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(sell -> sell.price),
                Codec.optionalField("next", Codec.pair(Codec.INT, Codec.STRING).listOf()).forGetter(sell -> sell.next)
        ).apply(instance, Buy::new));

        private long price;
        // If you buy A times, a sell element named B will be added.
        private Optional<List<Pair<Integer, String>>> next;

        public Buy(){
            this(ItemStack.EMPTY.copy(), 0, Optional.empty());
        }

        public Buy(ItemStack itemStack, long price, Optional<List<Pair<Integer, String>>> next) {
            super(itemStack);
            this.price = price;
            this.next = next;
        }

        public Buy(CompoundTag tag){
            super(tag);
        }

        public long getPrice() {
            return price;
        }

        @Override
        public void render(ItemRenderer itemRenderer, PoseStack poseStack, int x, int y) {
            super.render(itemRenderer, poseStack, x, y);
            TextComponent component = new TextComponent(price + " " + RpgwMod.CURRENCY);
            minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0x404040);
        }

        public TraderDataElement copy(){
            List<Pair<Integer, String>> list = next.orElse(null);
            if(list != null){
                List<Pair<Integer, String>> newList = new ArrayList<>();
                for(Pair<Integer, String> pair : list){
                    newList.add(new Pair<>(pair.getFirst(), pair.getSecond()));
                }
                return new Buy(itemStack.copy(), price, Optional.of(newList));
            } else return new Buy(itemStack.copy(), price, Optional.empty());
        }

        public List<Buy> confirmTrade(int count){
            List<Pair<Integer, String>> list = next.orElse(null);
            List<Buy> add = new ArrayList<>();
            if(list != null){
                List<Pair<Integer, String>> newList = new ArrayList<>();
                for(Pair<Integer, String> pair : list){
                    if(pair.getFirst() <= count){
                        Buy buy = TraderDataManager.instance.getBuy(pair.getSecond());
                        if(buy != null) add.add(buy);
                    } else{
                        newList.add(new Pair<>(pair.getFirst() - count, pair.getSecond()));
                    }
                }
                next = Optional.of(newList);
            }
            return add;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", this.price);
            List<Pair<Integer, String>> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putInt("Count", list.get(i).getFirst());
                    p.putString("Name", list.get(i).getSecond());
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
            if(nbt.contains("Next")){
                List<Pair<Integer, String>> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    Pair<Integer, String> pair = new Pair<>(p.getInt("Count"), p.getString("Name"));
                    list.add(pair);
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public static class Sell extends TraderDataElement {
        public static final Codec<Sell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Sell::getItemStack),
                Codec.LONG.fieldOf("price").forGetter(buy -> buy.price),
                Codec.INT.fieldOf("count").forGetter(buy -> buy.count),
                Codec.LONG.fieldOf("available_from").forGetter(buy -> buy.availableFrom.toEpochMilli()),
                Codec.LONG.fieldOf("delay").forGetter(buy -> buy.delay),
                Codec.optionalField("next", Codec.STRING.listOf()).forGetter(buy -> buy.next)
        ).apply(instance, Sell::new));
        private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        private long price;
        private int count;
        private Instant availableFrom;
        private long delay;
        // If you buy all of these items, this element will be deleted, and a buy element named this will be added.
        private Optional<List<String>> next;

        public Sell(){
            this(ItemStack.EMPTY.copy(), 0, 0, Instant.ofEpochMilli(0), 0, Optional.empty());
        }

        public Sell(ItemStack itemStack, long price, int count, Instant availableFrom, long delay, Optional<List<String>> next) {
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = availableFrom;
            this.delay = delay;
            this.next = next;
        }

        public Sell(CompoundTag tag){
            super(tag);
        }

        public Sell(ItemStack itemStack, long price, int count, long longAvailableFrom, long delay, Optional<List<String>> next){
            super(itemStack);
            this.price = price;
            this.count = count;
            this.availableFrom = Instant.ofEpochMilli(longAvailableFrom);
            this.delay = delay;
            this.next = next;
        }

        public long getPrice() { return price; }

        public int getCount() { return count; }

        public long getAvailableFrom(){ return availableFrom.toEpochMilli(); }

        public void setAvailableFrom(long milli){
            availableFrom = Instant.ofEpochMilli(milli);
        }

        public long getDelay(){
            return delay;
        }

        @Override
        public void render(ItemRenderer itemRenderer, PoseStack poseStack, int x, int y) {
            if(Instant.now().isAfter(availableFrom)){
                super.render(itemRenderer, poseStack, x, y);
                Component component = new TextComponent(price + " " + RpgwMod.CURRENCY);
                minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0x404040);
                String cnt = String.valueOf(count);
                if(count <= 3){
                    cnt = "§4" + cnt + "§8";
                }
                component = new TranslatableComponent("gui.rpgwmod.trader.upto", cnt);
                minecraft.font.draw(poseStack, component, x + 23, y + 7.5f, 0x555555);
            } else {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, LOCATION);
                GuiComponent.blit(poseStack, x, y, 114, 206, WIDTH, HEIGHT, 256, 256);
                itemRenderer.renderAndDecorateItem(itemStack, x + 3, y + 3);
                itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, x + 3, y + 3, null);

                long now = Instant.now().toEpochMilli();
                long s = (availableFrom.toEpochMilli() - now) / 1000;
                long m = s / 60;
                long h = m / 60;

                Component component = new TextComponent(price + " " + RpgwMod.CURRENCY);
                minecraft.font.draw(poseStack, component, x + WIDTH - 4 - minecraft.font.width(component), y + 7.5f, 0xbfbfbf);
                String cnt = String.valueOf(count);
                if(count <= 3){
                    cnt = "§4" + cnt + "§8";
                }
                component = new TranslatableComponent("gui.rpgwmod.trader.available", h > 0 ? h + "h" : m > 0 ? m + "m" : s + "s");
                minecraft.font.draw(poseStack, component, x + 23, y + 7.5f, 0xdddddd);
            }

        }

        public TraderDataElement copy(){
            List<String> list = next.orElse(null);
            if(list != null){
                List<String> newList = new ArrayList<>(list);
                return new Sell(itemStack.copy(), price, count, availableFrom, delay, Optional.of(newList));
            } else return new Sell(itemStack.copy(), price, count, Instant.ofEpochMilli(availableFrom.toEpochMilli()), delay, Optional.empty());
        }

        public List<Sell> confirmTrade(int count){
            this.count -= count;
            List<Sell> add = new ArrayList<>();
            if(this.count <= 0){
                List<String> list = next.orElse(null);
                if(list != null){
                    for(String key : list){
                        Sell sell = TraderDataManager.instance.getSell(key);
                        if(sell != null) add.add(sell);
                    }
                }
            }
            return add;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putLong("Price", price);
            tag.putInt("Count", count);
            tag.putLong("AvailableFrom", availableFrom.toEpochMilli());
            tag.putLong("Delay", delay);
            List<String> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putString("Name", list.get(i));
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            price = nbt.getLong("Price");
            count = nbt.getInt("Count");
            availableFrom = Instant.ofEpochMilli(nbt.getLong("AvailableFrom"));
            delay = nbt.getLong("Delay");
            if(nbt.contains("Next")){
                List<String> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    list.add(p.getString("Name"));
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public static class Barter extends TraderDataElement {
        public static final Codec<Barter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemstack").forGetter(Barter::getItemStack),
                ItemStack.CODEC.fieldOf("requirement").forGetter(barter -> barter.requirement),
                Codec.optionalField("next", Codec.pair(Codec.INT, Codec.STRING).listOf()).forGetter(barter -> barter.next)
        ).apply(instance, Barter::new));

        private ItemStack requirement;
        // If you buy A times, a sell element named B will be added.
        private Optional<List<Pair<Integer, String>>> next;

        public Barter(){
            this(ItemStack.EMPTY.copy(), ItemStack.EMPTY.copy(), Optional.empty());
        }

        public Barter(ItemStack itemStack, ItemStack requirement, Optional<List<Pair<Integer, String>>> next){
            super(itemStack);
            this.requirement = requirement;
            this.next = next;
        }

        public Barter(CompoundTag tag){
            super(tag);
        }

        public ItemStack getRequirement() {
            return requirement;
        }

        @Override
        public void render(ItemRenderer itemRenderer, PoseStack poseStack, int x, int y) {
            super.render(itemRenderer, poseStack, x, y);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, LOCATION);
            poseStack.pushPose();
            poseStack.translate(0, 0, -100);
            GuiComponent.blit(poseStack, x + 94, y + 2, 2, 208, 18, 18, 256, 256);
            poseStack.popPose();
            GuiComponent.blit(poseStack, x + 45, y + 3, 0, 228, 24, 16, 256, 256);
            itemRenderer.renderAndDecorateItem(requirement, x + 95, y + 3);
            itemRenderer.renderGuiItemDecorations(minecraft.font, requirement, x + 95, y + 3, null);
        }

        public TraderDataElement copy(){
            return new Barter(itemStack, requirement, next);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.put("Requirement", requirement.serializeNBT());
            List<Pair<Integer, String>> list = next.orElse(null);
            if(list != null){
                CompoundTag n = new CompoundTag();
                for(int i = 0; i < list.size(); i++){
                    CompoundTag p = new CompoundTag();
                    p.putInt("Count", list.get(i).getFirst());
                    p.putString("Name", list.get(i).getSecond());
                    n.put(String.format("%02d", i), p);
                }
                tag.put("Next", n);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            requirement = ItemStack.of(nbt.getCompound("Requirement"));
            if(nbt.contains("Next")){
                List<Pair<Integer, String>> list = new ArrayList<>();
                CompoundTag n = nbt.getCompound("Next");
                for(String key : n.getAllKeys()){
                    CompoundTag p = n.getCompound(key);
                    Pair<Integer, String> pair = new Pair<>(p.getInt("Count"), p.getString("Name"));
                    list.add(pair);
                }
                next = Optional.of(list);
            } else next = Optional.empty();
        }
    }

    public enum Type{
        Buy(0),
        Sell(1),
        Barter(2);

        public final int id;
        Type(int id){
            this.id = id;
        }

        public static Type fromId(int id){
            if(id == 0) return Buy;
            else if(id == 1) return Sell;
            else if(id == 2) return Barter;
            else return null;
        }
    }
}
