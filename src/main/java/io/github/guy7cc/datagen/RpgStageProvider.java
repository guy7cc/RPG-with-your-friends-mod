package io.github.guy7cc.datagen;

import com.mojang.serialization.Codec;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.RpgStage;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class RpgStageProvider extends RpgwSimpleDataProvider<RpgStage> {
    public RpgStageProvider(DataGenerator gen){
        super(gen);
    }

    @Override
    protected void register(Map<String, RpgStage> map){
        map.put("test_stage", new RpgStage(new TextComponent("Debug Stage"), List.of(new ResourceLocation(RpgwMod.MOD_ID, "default"))));
    }

    @Override
    protected Codec<RpgStage> getCodec() {
        return RpgStage.CODEC;
    }

    @Override
    protected String getFolder() {
        return "rpgdata/stage";
    }

    @Override
    protected String getNameForLog() {
        return "rpg stage";
    }

    @Override
    public String getName() {
        return "Rpg Stage: " + RpgwMod.MOD_ID;
    }
}
