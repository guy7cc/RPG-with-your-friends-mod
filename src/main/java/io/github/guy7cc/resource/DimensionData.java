package io.github.guy7cc.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record DimensionData(ResourceLocation key, String title, String subtitle) {
    public static final Codec<DimensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("key").forGetter(data -> data.key.toString()),
            Codec.STRING.fieldOf("title").forGetter(data -> data.title),
            Codec.STRING.fieldOf("subtitle").forGetter(data -> data.subtitle)
    ).apply(instance, DimensionData::new));

    public DimensionData(String key, String title, String subtitle){
        this(new ResourceLocation(key), title, subtitle);
    }

    //client-side
    public void showTitleIfKeyMatches(){
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gui.setTitle(new TextComponent(title));
        minecraft.gui.setSubtitle(new TextComponent(subtitle));
        minecraft.gui.setTimes(20, 70, 10);
    }
}
