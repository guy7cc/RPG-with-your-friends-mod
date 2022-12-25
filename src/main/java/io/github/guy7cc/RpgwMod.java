package io.github.guy7cc;

import com.mojang.logging.LogUtils;
import io.github.guy7cc.block.RpgwBlocks;
import io.github.guy7cc.block.entity.RpgwBlockEntities;
import io.github.guy7cc.item.RpgwItems;
import io.github.guy7cc.network.RpgwMessageManager;
import io.github.guy7cc.world.biome.RpgwBiomes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RpgwMod.MOD_ID)
public class RpgwMod
{
    public static final String MOD_ID = "rpgwmod";
    public static final String CURRENCY = "G";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public RpgwMod()
    {
        RpgwMessageManager.registerMessages("main");

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        RpgwBlocks.BLOCKS.register(bus);
        RpgwItems.ITEMS.register(bus);
        RpgwBlockEntities.BLOCK_ENTITIES.register(bus);
        RpgwBiomes.BIOMES.register(bus);
    }
}
