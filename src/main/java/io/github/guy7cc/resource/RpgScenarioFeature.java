package io.github.guy7cc.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.rpg.Party;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang3.NotImplementedException;

public abstract class RpgScenarioFeature {
    public static final Codec<RpgScenarioFeature> CODEC = CodecUtil.toParentCodec(RpgScenarioFeature.class,
            new CodecUtil.WithType<>(Adventure.CODEC, Adventure.class),
            new CodecUtil.WithType<>(KeepInventory.CODEC, KeepInventory.class)
    );

    public abstract void apply(Party party);

    public abstract Component getToolTip();

    public static class Adventure extends RpgScenarioFeature {
        public static final Codec<Adventure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("adventure").forGetter(f -> f.adventure)
        ).apply(instance, Adventure::new));

        private boolean adventure;

        public Adventure(boolean adventure){
            this.adventure = adventure;
        }

        public boolean isAdventure(){
            return adventure;
        }

        @Override
        public void apply(Party party) {
            if(party.isClientSide()) return;
            for(ServerPlayer player : party.getPlayers()){
                player.setGameMode(adventure ? GameType.ADVENTURE : GameType.SURVIVAL);
            }
        }

        @Override
        public Component getToolTip(){
            return new TranslatableComponent(adventure ? "gameMode.adventure" : "gameMode.survival");
        }
    }

    public static class KeepInventory extends RpgScenarioFeature {
        public static final Codec<KeepInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("keepInventory").forGetter(f -> f.keepInventory)
        ).apply(instance, KeepInventory::new));

        private boolean keepInventory;

        public KeepInventory(boolean keepInventory){
            this.keepInventory = keepInventory;
        }

        public boolean isInventoryKept(){
            return keepInventory;
        }

        @Override
        public void apply(Party party) {
            throw new NotImplementedException();
        }

        @Override
        public Component getToolTip(){
            return new TranslatableComponent(keepInventory ? "gamerule.keepInventory" : "rpgscenario.feature.noKeepInventory");
        }
    }
}
