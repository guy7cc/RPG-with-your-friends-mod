package io.github.guy7cc.block.entity;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, RpgwMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ExampleBorderBlockEntity>> EXAMPLE_BORDER = BLOCK_ENTITIES.register("example_border_block_entity",
            () -> BlockEntityType.Builder.of(ExampleBorderBlockEntity::new, RpgwBlocks.EXAMPLE_BORDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<RpgwSpawnerBlockEntity>> RPGW_SPAWNER = BLOCK_ENTITIES.register("rpgw_spawner_block_entity",
            () -> BlockEntityType.Builder.of(RpgwSpawnerBlockEntity::new, RpgwBlocks.RPGW_SPAWNER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BorderedRpgwSpawnerBlockEntity>> BORDERED_RPGW_SPAWNER = BLOCK_ENTITIES.register("bordered_rpgw_spawner_block_entity",
            () -> BlockEntityType.Builder.of(BorderedRpgwSpawnerBlockEntity::new, RpgwBlocks.BORDERED_RPGW_SPAWNER.get()).build(null));

    public static final RegistryObject<BlockEntityType<VendingMachineBlockEntity>> VENDING_MACHINE = BLOCK_ENTITIES.register("vending_machine_block_entity",
            () -> BlockEntityType.Builder.of(VendingMachineBlockEntity::new, RpgwBlocks.VENDING_MACHINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<RpgStageBlockEntity>> RPG_STAGE = BLOCK_ENTITIES.register("rpg_stage_block_entity",
            () -> BlockEntityType.Builder.of(RpgStageBlockEntity::new, RpgwBlocks.RPG_STAGE.get()).build(null));
}