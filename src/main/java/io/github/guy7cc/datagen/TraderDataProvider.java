package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.TraderDataElement;
import io.github.guy7cc.resource.TraderData;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TraderDataProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public TraderDataProvider(DataGenerator gen, ExistingFileHelper exFileHelper){
        generator = gen;
    }

    @Override
    public void run(HashCache pCache) {
        Map<String, TraderData> dataMap = new HashMap<>();
        Map<String, TraderDataElement.Sell> sellMap = new HashMap<>();
        Map<String, TraderDataElement.Buy> buyMap = new HashMap<>();
        Map<String, TraderDataElement.Barter> barterMap = new HashMap<>();

        registerData(dataMap);
        registerSell(sellMap);
        registerBuy(buyMap);
        registerBarter(barterMap);

        for(Map.Entry<String, TraderData> entry : dataMap.entrySet()){
            JsonObject json = (JsonObject)TraderData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().get();
            save(pCache, json, "traderdata", entry.getKey());
        }
        for(Map.Entry<String, TraderDataElement.Sell> entry : sellMap.entrySet()){
            JsonObject json = (JsonObject)TraderDataElement.Sell.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().get();
            save(pCache, json, "traderdata/sell", entry.getKey());
        }
        for(Map.Entry<String, TraderDataElement.Buy> entry : buyMap.entrySet()){
            JsonObject json = (JsonObject)TraderDataElement.Buy.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().get();
            save(pCache, json, "traderdata/buy", entry.getKey());
        }
        for(Map.Entry<String, TraderDataElement.Barter> entry : barterMap.entrySet()){
            JsonObject json = (JsonObject)TraderDataElement.Barter.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).result().get();
            save(pCache, json, "traderdata/barter", entry.getKey());
        }
    }

    private void registerData(Map<String, TraderData> dataMap){
        dataMap.put("test", new TraderData(
                new ArrayList<>(Arrays.asList(
                        new TraderDataElement.Sell(new ItemStack(Items.NAME_TAG), 1000, Optional.empty())
                )),
                new ArrayList<>(Arrays.asList(
                        new TraderDataElement.Buy(new ItemStack(Items.WHEAT), 3, 10, new Date(0), 0, Optional.empty())
                )),
                new ArrayList<>(Arrays.asList(
                        new TraderDataElement.Barter(new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.TROPICAL_FISH), Optional.empty())
                ))
        ));
    }

    private void registerSell(Map<String, TraderDataElement.Sell> sellMap){
        sellMap.put("selltest", new TraderDataElement.Sell(new ItemStack(Items.ICE), 100, Optional.empty()));
    }

    private void registerBuy(Map<String, TraderDataElement.Buy> buyMap){

    }

    private void registerBarter(Map<String, TraderDataElement.Barter> barterMap){

    }

    public void save(HashCache cache, JsonObject stateJson, String folder, String name){
        Path mainOutput = generator.getOutputFolder();
        String pathSuffix = "data/" + RpgwMod.MOD_ID + "/" + folder + "/" + name + ".json";
        Path outputPath = mainOutput.resolve(pathSuffix);
        try {
            DataProvider.save(GSON, cache, stateJson, outputPath);
        } catch (IOException e) {
            LOGGER.error("Couldn't save trader data to {}", outputPath, e);
        }
    }

    @Override
    public String getName() {
        return "Trader Data: " + RpgwMod.MOD_ID;
    }
}
