package io.github.guy7cc.datagen.lang;

import io.github.guy7cc.RpgwMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class RpgwAbstractLanguageProvider extends LanguageProvider {
    public RpgwAbstractLanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    public void addItemGroup(String key, String name){
        add("itemGroup." + key, name);
    }

    public void addGui(String key, String value){
        add("gui." + RpgwMod.MOD_ID + "." + key, value);
    }

    public void addCommands(String command, String key, String value){
        add("commands." + command + "." + key, value);
    }
}
