package io.github.guy7cc.rpg;

import java.util.function.Function;

public class ElementModifier {
    public ElementType type;
    public ElementModifierType modifierType;
    public Function<Float, Float> func;
    public boolean absolute;

    public ElementModifier(ElementType type, ElementModifierType modifierType, Function<Float, Float> func){
        this(type, modifierType, func, false);
    }

    public ElementModifier(ElementType type, ElementModifierType modifierType, Function<Float, Float> func, boolean absolute){
        this.type = type;
        this.modifierType = modifierType;
        this.func = func;
        this.absolute = absolute;
    }
}
