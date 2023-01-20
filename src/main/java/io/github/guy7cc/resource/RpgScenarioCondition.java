package io.github.guy7cc.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.rpg.Party;
import io.github.guy7cc.save.RpgSavedData;
import io.github.guy7cc.save.RpgScenarioSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.List;

public abstract class RpgScenarioCondition {
    public static final Codec<RpgScenarioCondition> CODEC = CodecUtil.toParentCodec(RpgScenarioCondition.class,
            new CodecUtil.WithType<>(MaxPlayer.CODEC, MaxPlayer.class),
            new CodecUtil.WithType<>(PassedScenario.CODEC, PassedScenario.class),
            new CodecUtil.WithType<>(AllowedItems.CODEC, AllowedItems.class),
            new CodecUtil.WithType<>(BannedItems.CODEC, BannedItems.class)
    );

    // Optional.empty() means passing the test
    public abstract Optional<Component> test(Party party);

    public abstract void render(PoseStack poseStack, int x, int y, int width);

    public static class MaxPlayer extends RpgScenarioCondition{
        public static final Codec<MaxPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("max").forGetter(i -> i.max)
        ).apply(instance, MaxPlayer::new));

        private final int max;

        public MaxPlayer(int max){
            this.max = max;
        }

        @Override
        public Optional<Component> test(Party party){
            if(party.isClientSide()){
                return Optional.empty();
            } else {
                if(party.size() <= max) return Optional.empty();
                else return Optional.of(new TranslatableComponent("rpgscenario.condition.maxPlayer.failed"));
            }
        }

        @Override
        public void render(PoseStack poseStack, int x, int y, int width) {
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> sequences = ComponentRenderUtils.wrapComponents(new TranslatableComponent("rpgscenario.condition.maxPlayer.require").append(Integer.toString(max)), width, Minecraft.getInstance().font);
            for(FormattedCharSequence s : sequences){
                font.drawShadow(poseStack, s, x, y, 0xffffff);
            }
        }
    }

    public static class PassedScenario extends RpgScenarioCondition{
        public static final Codec<PassedScenario> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("scenario").forGetter(i -> i.list)
        ).apply(instance, PassedScenario::new));

        private ImmutableList<ResourceLocation> list;
        private Component requirement;

        public PassedScenario(List<ResourceLocation> scenario){
            this.list = ImmutableList.copyOf(scenario);
            MutableComponent component = new TranslatableComponent("rpgscenario.condition.passedScenario.require")
                    .append("\n ")
                    .append(Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(list.get(0)).title()));
            for(int i = 1; i < list.size(); i++){
                component.append("\n ").append(Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(list.get(i)).title()));
            }
            requirement = component;
        }

        @Override
        public Optional<Component> test(Party party){
            if(party.isClientSide()) return Optional.empty();

            MinecraftServer server = party.getServer();
            RpgSavedData savedData = RpgSavedData.get(server);
            MutableComponent component = null;
            for(ResourceLocation scenarioLoc : list){
                RpgScenarioSavedData scenarioSavedData = savedData.getOrCreateRpgScenarioSavedData(scenarioLoc);
                List<Component> neverPassedNames = new ArrayList<>();
                for(ServerPlayer player : party.getPlayers()){
                    if(!scenarioSavedData.passed(player)) neverPassedNames.add(player.getName());
                }
                if(neverPassedNames.size() > 0){
                    MutableComponent names = new TextComponent("").append(neverPassedNames.get(0));
                    for(int i = 1; i < neverPassedNames.size(); i++){
                        names.append(", ").append(neverPassedNames.get(i));
                    }
                    Component scenarioName = Component.Serializer.fromJsonLenient(RpgScenarioManager.instance.getOrDefault(scenarioLoc).title());
                    MutableComponent hasNeverPassedMessage = new TranslatableComponent("rpgscenario.condition.passedScenario.neverPassed", names).append(scenarioName);
                    if(component == null) component = hasNeverPassedMessage;
                    else component.append("\n").append(hasNeverPassedMessage);
                }
            }
            return component == null ? Optional.empty() : Optional.of(component);
        }

        @Override
        public void render(PoseStack poseStack, int x, int y, int width){
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> sequences = ComponentRenderUtils.wrapComponents(requirement, width, Minecraft.getInstance().font);
            for(FormattedCharSequence s : sequences){
                font.drawShadow(poseStack, s, x, y, 0xffffff);
            }
        }
    }

    public static class AllowedItems extends RpgScenarioCondition{
        public static final Codec<AllowedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("allowed").forGetter(i -> i.list)
        ).apply(instance, AllowedItems::new));

        private final ImmutableList<ItemStack> list;

        public AllowedItems(List<ItemStack> list){
            this.list = ImmutableList.copyOf(list);
        }


        @Override
        public Optional<Component> test(Party party){
            if(!party.isClientSide()) return Optional.empty();
            MutableComponent component = null;
            LocalPlayer player = Minecraft.getInstance().player;
            List<ItemStack> copyList = list.stream().map(is -> is.copy()).toList();
            for (ItemStack is : player.getInventory().items) {
                boolean pass = false;
                int i = 0;
                for (; i < copyList.size(); i++) {
                    ItemStack allowed = copyList.get(i);
                    if (is.sameItem(allowed)) {
                        if (is.getCount() <= allowed.getCount()) {
                            allowed.shrink(is.getCount());
                            pass = true;
                        }
                        break;
                    }
                }
                if (!pass) {
                    if (i == copyList.size()) {
                        if (component == null)
                            component = new TranslatableComponent("rpgscenario.condition.allowedItems.notAllowed", is.getItem().getName(is));
                        else
                            component.append("\n").append(new TranslatableComponent("rpgscenario.condition.allowedItems.notAllowed", is.getItem().getName(is)));
                    } else {
                        if (component == null)
                            component = new TranslatableComponent("rpgscenario.condition.allowedItems.upTo", is.getItem().getName(is), list.get(i).getCount());
                        else
                            component.append("\n").append(new TranslatableComponent("rpgscenario.condition.allowedItems.upTo", is.getItem().getName(is), list.get(i).getCount()));
                    }
                }
            }
            return Optional.of(component);


        }

        @Override
        public void render(PoseStack poseStack, int x, int y, int width){

        }
    }

    public static class BannedItems extends RpgScenarioCondition{
        public static final Codec<BannedItems> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("banned").forGetter(i -> i.list)
        ).apply(instance, BannedItems::new));

        private final ImmutableList<ItemStack> list;

        public BannedItems(List<ItemStack> list){
            this.list = ImmutableList.copyOf(list);
        }

        @Override
        public Optional<Component> test(Party party){
            if(!party.isClientSide()) return Optional.empty();
            MutableComponent component = null;
            LocalPlayer player = Minecraft.getInstance().player;
            List<ItemStack> copyList = list.stream().map(is -> is.copy()).toList();
            for(ItemStack is : player.getInventory().items){
                boolean pass = true;
                int i = 0;
                for(; i < copyList.size(); i++){
                    ItemStack banned = copyList.get(i);
                    if(is.sameItem(banned)){
                        if(is.getCount() < banned.getCount()) banned.shrink(is.getCount());
                        else pass = false;
                        break;
                    }
                }
                if(!pass){
                    if (component == null)
                        component = new TranslatableComponent("rpgscenario.condition.bannedItems.banned", is.getItem().getName(is), list.get(i).getCount());
                    else
                        component.append("\n").append(new TranslatableComponent("rpgscenario.condition.bannedItems.banned", is.getItem().getName(is), list.get(i).getCount()));
                }
            }
            return Optional.of(component);
        }

        @Override
        public void render(PoseStack poseStack, int x, int y, int width){

        }
    }
}
