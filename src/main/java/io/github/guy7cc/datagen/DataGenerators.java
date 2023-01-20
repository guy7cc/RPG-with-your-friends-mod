package io.github.guy7cc.datagen;

import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.datagen.lang.RpgwEnUsLanguageProvider;
import io.github.guy7cc.datagen.lang.RpgwJaJpLanguageProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = RpgwMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(new RpgwBlockStateProvider(generator, RpgwMod.MOD_ID, existingFileHelper));
        generator.addProvider(new RpgwItemModelProvider(generator, RpgwMod.MOD_ID, existingFileHelper));
        generator.addProvider(new RpgwEnUsLanguageProvider(generator, RpgwMod.MOD_ID));
        generator.addProvider(new RpgwJaJpLanguageProvider(generator, RpgwMod.MOD_ID));
        generator.addProvider(new DimensionDataProvider(generator));
        generator.addProvider(new RpgStageProvider(generator));
        generator.addProvider(new RpgScenarioProvider(generator));
        generator.addProvider(new TraderDataProvider(generator, existingFileHelper));
    }
}
