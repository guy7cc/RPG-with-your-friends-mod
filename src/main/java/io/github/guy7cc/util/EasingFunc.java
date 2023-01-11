package io.github.guy7cc.util;

import java.util.function.Function;

public class EasingFunc {
    public static Function<Float, Float> IDENTITY = x -> x;

    public static Function<Float, Float> getArcSigmoid(float gain){
        double A = Math.exp(-gain);
        double C = (1+A) * (1+A) / (1-A*A);
        double offset = A * (1+A) / (1-A*A);
        return x -> (float)(-(1 / (2 * gain)) * Math.log(C / (x + offset) - 1) + 0.5);
    }

    //https://github.com/ai/easings.net

    public static double easeOutQuint(double t) {
        return 1 - Math.pow(1 - t, 5);
    }

    public static double easeInOutCubic(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }
}
