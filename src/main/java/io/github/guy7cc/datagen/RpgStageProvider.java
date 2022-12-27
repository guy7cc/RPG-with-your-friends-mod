package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.DimensionData;
import io.github.guy7cc.resource.RpgLevel;
import io.github.guy7cc.resource.RpgStage;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgStageProvider extends RpgwSimpleDataProvider<RpgStage> {
    public RpgStageProvider(DataGenerator gen){
        super(gen);
    }

    @Override
    protected void register(Map<String, RpgStage> map){
        map.put("test_stage", new RpgStage("Debug Stage", List.of("test_level")));
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
