package io.github.zap.commons.utils;

public class MathUtils {
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double normalizeMultiplier(double value, double upperValue) {
        if (value > 0) {
            return clamp(value, 0, upperValue);
        } else {
            return upperValue - clamp(-value, 0, upperValue);
        }
    }

    public static long longFromInts(int x, int z) {
        return (((long)x) << 32) | (z & 0xFFFFFFFFL);
    }
}
