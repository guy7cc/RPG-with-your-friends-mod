package io.github.guy7cc.item;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.block.RpgwBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RpgwItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RpgwMod.MOD_ID);

    //items
    public static final RegistryObject<Item> BORDER_WRENCH = ITEMS.register("border_wrench", () -> new BorderWrenchItem(defaultProperties()));

    public static final RegistryObject<Item> SPAWNER_WRENCH = ITEMS.register("spawner_wrench", () -> new SpawnerWrenchItem(defaultProperties()));

    public static final RegistryObject<Item> IRON_COIN = ITEMS.register("iron_coin", () -> new CoinItem(CoinItem.Rank.IRON, defaultProperties()));
    public static final RegistryObject<Item> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(CoinItem.Rank.COPPER, defaultProperties()));
    public static final RegistryObject<Item> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(CoinItem.Rank.SILVER, defaultProperties()));
    public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("gold_coin", () -> new CoinItem(CoinItem.Rank.GOLD, defaultProperties()));

    public static CreativeModeTab creativeTab = new CreativeModeTab(RpgwMod.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BORDER_WRENCH.get());
        }
    };

    public static Item.Properties defaultProperties() {
        return new Item.Properties().tab(creativeTab);
    }

    public static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block){
        ITEMS.register(name, () -> new BlockItem(block.get(), defaultProperties()));
    }
}
