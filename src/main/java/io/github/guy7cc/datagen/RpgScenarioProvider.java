package io.github.guy7cc.datagen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.*;
import io.github.guy7cc.world.dimension.RpgwDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RpgScenarioProvider extends RpgwSimpleDataProvider<RpgScenario> {
    public RpgScenarioProvider(DataGenerator gen){
        super(gen);
    }

    @Override
    protected void register(Map<String, RpgScenario> map){
        map.put("test_scenario",
                new RpgScenario(
                        "Debug Level",
                        "No Info",
                        new BlockPos(0, 100, 0),
                        RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
                        List.of(
                                new RpgScenarioFeature.Adventure(true),
                                new RpgScenarioFeature.KeepInventory(true)
                        ),
                        List.of(
                                new RpgScenarioCondition.MaxPlayer(3)
                        ),
                        List.of(
                                new RpgScenarioSetUp.SetBlock(
                                        RpgwDimensions.RPGW_DEBUG_DIMENSION_KEY.location(),
                                        Blocks.STONE_BRICK_SLAB.defaultBlockState(),
                                        new BlockPos(0, 99, 0),
                                        Optional.empty()
                                )
                        ),
                        List.of(
                                new RpgScenarioReward.Exp(100),
                                new RpgScenarioReward.Items(
                                        List.of(
                                                Pair.of(0, new ItemStack(Items.DIAMOND_AXE)),
                                                Pair.of(1, new ItemStack(Items.NAME_TAG, 3))
                                        )
                                )
                        ),
                        List.of(
                                new RpgScenarioReward.Exp(10)
                        )
                )
        );
    }

    @Override
    protected Codec<RpgScenario> getCodec() {
        return RpgScenario.CODEC;
    }

    @Override
    protected String getFolder() {
        return "rpgdata/scenario";
    }

    @Override
    protected String getNameForLog() {
        return "rpg scenario";
    }

    @Override
    public String getName() {
        return "Rpg Scenario: " + RpgwMod.MOD_ID;
    }
}
