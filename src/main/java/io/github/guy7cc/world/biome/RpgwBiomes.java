package io.github.guy7cc.world.biome;

import io.github.guy7cc.RpgwMod;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, RpgwMod.MOD_ID);

    public static final RegistryObject<Biome> RPGW_DEBUG_BIOME = BIOMES.register("rpgw_debug_biome", () ->
        Biome.BiomeBuilder.from(OverworldBiomes.theVoid())
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .skyColor(15608638)
                        .fogColor(16761538)
                        .waterColor(4187232)
                        .waterFogColor(10998700)
                        .build())
                .build()
    );
}
