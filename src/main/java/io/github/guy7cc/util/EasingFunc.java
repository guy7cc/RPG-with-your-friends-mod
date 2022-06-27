package io.github.guy7cc.util;

import java.util.Random;
import java.util.function.Function;

public class EasingFunc {
    public static Function<Float, Float> IDENTITY = x -> x;

    public static Function<Float, Float> getArcSigmoid(float gain){
        double A = Math.exp(-gain);
        double C = (1+A) * (1+A) / (1-A*A);
        double offset = A * (1+A) / (1-A*A);
        return x -> (float)(-(1 / (2 * gain)) * Math.log(C / (x + offset) - 1) + 0.5);
    }
}
