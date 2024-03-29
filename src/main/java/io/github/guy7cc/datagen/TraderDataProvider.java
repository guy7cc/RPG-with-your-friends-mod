package io.github.guy7cc.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.guy7cc.RpgwMod;
import io.github.guy7cc.resource.TraderDataElement;
import io.github.guy7cc.resource.TraderData;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
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
        Map<String, TraderDataElement.Buy> buyMap = new HashMap<>();
        Map<String, TraderDataElement.Sell> sellMap = new HashMap<>();
        Map<String, TraderDataElement.Barter> barterMap = new HashMap<>();

        registerBuy(buyMap);
        registerSell(sellMap);
        registerBarter(barterMap);
        registerData(dataMap, buyMap, sellMap, barterMap);

        for(Map.Entry<String, TraderData> entry : dataMap.entrySet()){
            DataResult<JsonElement> result = TraderData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, "traderdata", entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
        for(Map.Entry<String, TraderDataElement.Buy> entry : buyMap.entrySet()){
            DataResult<JsonElement> result = TraderDataElement.Buy.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, "traderdata/buy", entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
        for(Map.Entry<String, TraderDataElement.Sell> entry : sellMap.entrySet()){
            DataResult<JsonElement> result = TraderDataElement.Sell.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, "traderdata/sell", entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
        for(Map.Entry<String, TraderDataElement.Barter> entry : barterMap.entrySet()){
            DataResult<JsonElement> result = TraderDataElement.Barter.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            if(result.result().isPresent()){
                JsonObject json = (JsonObject)result.result().get();
                save(pCache, json, "traderdata/barter", entry.getKey());
            } else {
                LOGGER.error(result.error().get().message());
            }
        }
    }

    private void registerData(Map<String, TraderData> dataMap, Map<String, TraderDataElement.Buy> buyMap, Map<String, TraderDataElement.Sell> sellMap, Map<String, TraderDataElement.Barter> barterMap){
        //[0-9/._-] and small letters
        dataMap.put("test", new TraderData(
                new ArrayList<>(Arrays.asList(
                        new TraderDataElement.Buy(new ItemStack(Items.NAME_TAG, 2), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.RED_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.PINK_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.YELLOW_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.GREEN_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.BLUE_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.LIGHT_BLUE_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.PURPLE_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.BLACK_DYE), 1000, Optional.empty()),
                        new TraderDataElement.Buy(new ItemStack(Items.WHITE_DYE), 1000, Optional.empty())
                )),
                new ArrayList<>(Arrays.asList(
                        sellMap.get("sell_wheat")
                )),
                new ArrayList<>(Arrays.asList(
                        barterMap.get("bartertest")
                ))
        ));
    }

    private void registerBuy(Map<String, TraderDataElement.Buy> buyMap){
        //[a-z0-9/._-]
        buyMap.put("buytest", new TraderDataElement.Buy(new ItemStack(Items.ICE), 100, Optional.empty()));
    }

    private void registerSell(Map<String, TraderDataElement.Sell> sellMap){
        //[a-z0-9/._-]
        sellMap.put("sell_wheat", new TraderDataElement.Sell(new ItemStack(Items.WHEAT), 3, 2, 0, 10000, Optional.of(
                List.of(
                        new ResourceLocation(RpgwMod.MOD_ID, "sell_wheat")
                )
        )));
        sellMap.put("selltest", new TraderDataElement.Sell(new ItemStack(Items.BELL), 250, 5, Instant.ofEpochMilli(0), 10000, Optional.empty()));
    }

    private void registerBarter(Map<String, TraderDataElement.Barter> barterMap){
        //[a-z0-9/._-]
        barterMap.put("bartertest", new TraderDataElement.Barter(new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.TROPICAL_FISH), Optional.of(
                List.of(
                        new Pair<>(1, new ResourceLocation(RpgwMod.MOD_ID, "bartertest"))
                )
        )));
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
