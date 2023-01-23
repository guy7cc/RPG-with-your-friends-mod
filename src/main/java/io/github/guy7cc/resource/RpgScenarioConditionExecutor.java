package io.github.guy7cc.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class RpgScenarioConditionExecutor {
    public static Optional<Component> test(RpgScenarioCondition.MaxPlayer condition){
        return Optional.empty();
    }

    public static Optional<Component> test(RpgScenarioCondition.PassedScenario condition){
        return Optional.empty();
    }

    public static Optional<Component> test(RpgScenarioCondition.AllowedItems condition){
        MutableComponent component = null;
        LocalPlayer player = Minecraft.getInstance().player;
        List<ItemStack> copyList = condition.getList().stream().map(is -> is.copy()).toList();
        for (ItemStack is : player.getInventory().items) {
            boolean pass = false;
            int i = 0;
            for (; i < copyList.size(); i++) {
                ItemStack allowed = copyList.get(i);
                if (is.sameItem(allowed)) {
                    if (is.getCount() <= allowed.getCount()) {
                        allowed.shrink(is.getCount());
                        pass = true;
                    }
                    break;
                }
            }
            if (!pass) {
                if (i == copyList.size()) {
                    if (component == null)
                        component = new TranslatableComponent("rpgscenario.condition.allowedItems.notAllowed", is.getItem().getName(is));
                    else
                        component.append("\n").append(new TranslatableComponent("rpgscenario.condition.allowedItems.notAllowed", is.getItem().getName(is)));
                } else {
                    if (component == null)
                        component = new TranslatableComponent("rpgscenario.condition.allowedItems.upTo", is.getItem().getName(is), condition.getList().get(i).getCount());
                    else
                        component.append("\n").append(new TranslatableComponent("rpgscenario.condition.allowedItems.upTo", is.getItem().getName(is), condition.getList().get(i).getCount()));
                }
            }
        }
        return Optional.of(component);
    }

    public static Optional<Component> test(RpgScenarioCondition.BannedItems condition){
        MutableComponent component = null;
        LocalPlayer player = Minecraft.getInstance().player;
        List<ItemStack> copyList = condition.getList().stream().map(is -> is.copy()).toList();
        for(ItemStack is : player.getInventory().items){
            boolean pass = true;
            int i = 0;
            for(; i < copyList.size(); i++){
                ItemStack banned = copyList.get(i);
                if(is.sameItem(banned)){
                    if(is.getCount() < banned.getCount()) banned.shrink(is.getCount());
                    else pass = false;
                    break;
                }
            }
            if(!pass){
                if (component == null)
                    component = new TranslatableComponent("rpgscenario.condition.bannedItems.banned", is.getItem().getName(is), condition.getList().get(i).getCount());
                else
                    component.append("\n").append(new TranslatableComponent("rpgscenario.condition.bannedItems.banned", is.getItem().getName(is), condition.getList().get(i).getCount()));
            }
        }
        return Optional.of(component);
    }
}
