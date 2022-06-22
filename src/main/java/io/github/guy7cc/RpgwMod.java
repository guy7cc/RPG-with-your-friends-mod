package io.github.guy7cc;

import com.mojang.logging.LogUtils;
import io.github.guy7cc.network.RpgwMessageManager;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RpgwMod.MOD_ID)
public class RpgwMod
{
    public static final String MOD_ID = "rpgwmod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public RpgwMod()
    {
        RpgwMessageManager.registerMessages("main");
    }
}
