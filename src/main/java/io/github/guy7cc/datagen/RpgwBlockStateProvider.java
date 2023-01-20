package io.github.guy7cc.datagen;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.block.VendingMachineBlock;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RpgwBlockStateProvider extends BlockStateProvider {
    public RpgwBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(RpgwBlocks.EXAMPLE_BORDER.get());

        simpleBlockWithItem(RpgwBlocks.RPGW_SPAWNER.get());
        simpleBlockWithItem(RpgwBlocks.BORDERED_RPGW_SPAWNER.get());
        simpleBlockWithItem(RpgwBlocks.INACTIVATED_RPGW_SPAWNER.get());

        ResourceLocation name = RpgwBlocks.VENDING_MACHINE.get().getRegistryName();
        doubleOrientableBlockWithItem(RpgwBlocks.VENDING_MACHINE.get(),
                blockTexture(modLoc(name.getPath() + "_upper_side")),
                blockTexture(modLoc(name.getPath() + "_upper_front")),
                blockTexture(Blocks.IRON_BLOCK),
                blockTexture(modLoc(name.getPath() + "_lower_side")),
                blockTexture(modLoc(name.getPath() + "_lower_front")),
                blockTexture(Blocks.IRON_BLOCK));

        Block block = RpgwBlocks.RPG_STAGE.get();
        ModelFile modelFile = models().getExistingFile(block.getRegistryName());
        itemModels().withExistingParent(block.getRegistryName().getPath(), modelFile.getLocation());
        horizontalBlock(block, modelFile);
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void doubleOrientableBlockWithItem(Block block, ResourceLocation upperSide, ResourceLocation upperFront, ResourceLocation upperTop, ResourceLocation lowerSide, ResourceLocation lowerFront, ResourceLocation lowerTop){
        String name = block.getRegistryName().getPath();
        ModelFile modelUpper = models().orientable(name + "_upper", upperSide, upperFront, upperTop);
        ModelFile modelLower = models().orientable(name + "_lower", lowerSide, lowerFront, lowerTop);
        itemModels().basicItem(block.getRegistryName());
        getVariantBuilder(block).forAllStates(state -> {
            int yRot = ((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()) + 180;
            yRot %= 360;
            return ConfiguredModel.builder().modelFile(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? modelUpper : modelLower)
                    .rotationY(yRot)
                    .build();
        });

    }

    public ResourceLocation blockTexture(ResourceLocation name) {
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }
}
