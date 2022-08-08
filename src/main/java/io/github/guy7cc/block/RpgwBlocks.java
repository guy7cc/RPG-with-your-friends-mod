package io.github.guy7cc.block;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.item.RpgwItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RpgwBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RpgwMod.MOD_ID);

    //block

    //block entity
    public static final RegistryObject<ExampleBorderBlock> EXAMPLE_BORDER = registerBlockWithItem("example_border_block", () -> new ExampleBorderBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE)));
    public static final RegistryObject<RpgwSpawnerBlock> RPGW_SPAWNER = registerBlockWithItem("rpgw_spawner", () -> new RpgwSpawnerBlock(BlockBehaviour.Properties.copy(Blocks.SPAWNER)));
    public static final RegistryObject<BorderedRpgwSpawnerBlock> BORDERED_RPGW_SPAWNER = registerBlockWithItem("bordered_rpgw_spawner", () -> new BorderedRpgwSpawnerBlock(BlockBehaviour.Properties.copy(Blocks.SPAWNER)));

    public static <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block){
        RegistryObject<T> registryObject = BLOCKS.register(name, block);
        RpgwItems.registerBlockItem(name, registryObject);
        return registryObject;
    }
}
