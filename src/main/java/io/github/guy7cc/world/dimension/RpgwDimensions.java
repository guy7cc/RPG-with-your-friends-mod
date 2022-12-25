package io.github.guy7cc.world.dimension;

import io.github.guy7cc.RpgwMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class RpgwDimensions {
    public static ResourceKey<Level> RPGW_DEBUG_DIMENSION_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY,
            new ResourceLocation(RpgwMod.MOD_ID, "rpgw_debug_dimension"));
    public static ResourceKey<DimensionType> RPGW_DEBUG_DIMENSION_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
            RPGW_DEBUG_DIMENSION_KEY.getRegistryName());
}
