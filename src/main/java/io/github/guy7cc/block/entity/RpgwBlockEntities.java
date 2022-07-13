package io.github.guy7cc.block.entity;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, RpgwMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ExampleBorderBlockEntity>> EXAMPLE_BORDER_BLOCK_ENTITY = BLOCK_ENTITIES.register("example_border_block_entity",
            () -> BlockEntityType.Builder.of(ExampleBorderBlockEntity::new, RpgwBlocks.EXAMPLE_BORDER_BLOCK.get()).build(null));
}