package io.github.guy7cc.datagen;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.item.RpgwItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RpgwItemModelProvider extends ItemModelProvider {
    public RpgwItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(RpgwItems.DEBUG_WRENCH.get());

        simpleItem(RpgwItems.IRON_COIN.get());
        simpleItem(RpgwItems.COPPER_COIN.get());
        simpleItem(RpgwItems.SILVER_COIN.get());
        simpleItem(RpgwItems.GOLD_COIN.get());
    }

    private ItemModelBuilder simpleItem(Item item){
        return withExistingParent(item.getRegistryName().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(RpgwMod.MOD_ID, "item/" + item.getRegistryName().getPath()));
    }

    private ItemModelBuilder handheldItem(Item item){
        return withExistingParent(item.getRegistryName().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(RpgwMod.MOD_ID, "item/" + item.getRegistryName().getPath()));
    }
}
