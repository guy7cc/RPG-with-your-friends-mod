package io.github.guy7cc.item;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RpgwMod.MOD_ID);

    public static final RegistryObject<Item> EXAMPLE_BORDER_BLOCK_ITEM = ITEMS.register("example_border_block", () -> new BlockItem(RpgwBlocks.EXAMPLE_BORDER_BLOCK.get(), defaultProperties()));

    public static final RegistryObject<Item> BORDER_WRENCH = ITEMS.register("border_wrench", () -> new Item(defaultProperties()));

    public static CreativeModeTab creativeTab = new CreativeModeTab(RpgwMod.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BORDER_WRENCH.get());
        }
    };

    public static Item.Properties defaultProperties() {
        return new Item.Properties().tab(creativeTab);
    }
}
