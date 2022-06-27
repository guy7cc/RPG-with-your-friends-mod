package io.github.guy7cc.rpg;

import io.github.guy7cc.util.EasingFunc;

import java.util.*;
import java.util.function.Function;

public class ElementValue {
    private static final Random random = new Random();
    private Map<ElementType, Node> valueMap;

    public ElementValue(){
        valueMap = new HashMap<>();
    }

    public ElementValue with(ElementType type, float minValue, float maxValue, Function<Float, Float> defaultActivator){
        valueMap.put(type, new Node(minValue, maxValue, defaultActivator));
        return this;
    }

    public void modify(ElementModifier modifier){
        if(!valueMap.containsKey(modifier.type))
            with(modifier.type, 0f, 0f, EasingFunc.IDENTITY);
        List<Function<Float, Float>> list = modifier.modifierType == ElementModifierType.VALUE
                ? valueMap.get(modifier.type).valueModifierList
                : valueMap.get(modifier.type).activatorList;
        if(modifier.absolute) list.clear();
        list.add(modifier.func);
    }

    public float getValue(ElementType type){
        return valueMap.containsKey(type) ? valueMap.get(type).getValue() : 0f;
    }

    private static class Node{
        public float minValue;
        public float maxValue;
        public List<Function<Float, Float>> valueModifierList;
        public List<Function<Float, Float>> activatorList;
        public Node(float minValue, float maxValue, Function<Float, Float> defaultActivator){
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.valueModifierList = new ArrayList<>();
            this.valueModifierList.add(EasingFunc.IDENTITY);
            this.activatorList = new ArrayList<>();
            this.activatorList.add(defaultActivator);
        }

        public float getValue() {
            float realMinValue = minValue;
            float realMaxValue = maxValue;
            valueModifierList.forEach(f -> {
                f.apply(realMinValue);
                f.apply(realMaxValue);
            });
            float rate = random.nextFloat(1f);
            activatorList.forEach(f -> f.apply(rate));
            return minValue + rate * (maxValue - minValue);
        }
    }
}
