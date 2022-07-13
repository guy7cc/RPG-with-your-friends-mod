package io.github.guy7cc.block;

import io.github.guy7cc.RpgwMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RpgwMod.MOD_ID);

    public static final RegistryObject<ExampleBorderBlock> EXAMPLE_BORDER_BLOCK = BLOCKS.register("example_border_block", () -> new ExampleBorderBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE)));
}
