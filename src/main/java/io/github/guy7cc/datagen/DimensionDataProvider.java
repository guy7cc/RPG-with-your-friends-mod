package io.github.guy7cc.datagen;

import com.mojang.serialization.Codec;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.DimensionData;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.data.DataGenerator;

import java.util.Map;

public class DimensionDataProvider extends RpgwSimpleDataProvider<DimensionData> {
    public DimensionDataProvider(DataGenerator gen){
        super(gen);
    }

    @Override
    protected void register(Map<String, DimensionData> map){
        map.put("test", new DimensionData(RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(), "Debug World", "Coordinate (∞, 0)"));
    }

    @Override
    protected Codec<DimensionData> getCodec() {
        return DimensionData.CODEC;
    }

    @Override
    protected String getFolder() {
        return "rpgdata/dimension";
    }

    @Override
    protected String getNameForLog() {
        return "dimension data";
    }

    @Override
    public String getName() {
        return "Dimension Data: " + RpgwMod.MOD_ID;
    }
}
