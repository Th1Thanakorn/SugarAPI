package com.thana.sugarapi.common.utils;

import net.minecraft.util.Mth;

public class ARGBHelper {

    public static int alpha(int alpha) {
        return alpha >>> 24;
    }

    public static int red(int red) {
        return red >> 16 & 255;
    }

    public static int green(int green) {
        return green >> 8 & 255;
    }

    public static int blue(int blue) {
        return blue & 255;
    }

    public static int to32BitColor(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int multiply(int i, int j) {
        return to32BitColor(alpha(i) * alpha(j) / 255, red(i) * red(j) / 255, green(i) * green(j) / 255, blue(i) * blue(j) / 255);
    }

    public static int toChatColor(int a, int r, int g, int b) {
        int rgb = ARGBHelper.toChatColor(r, g, b);
        int alpha = a << 24 & -16777216;
        return rgb | alpha;
    }

    public static int toChatColor(int r, int g, int b) {
        return r * 65536 + g * 256 + b;
    }

    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String fastRgbToHex(int r, int g, int b) {
        return "#" + Integer.toHexString(toChatColor(r, g, b));
    }

    public static String fastArgbToHex(int a, int r, int g, int b) {
        return "#" + Integer.toHexString(toChatColor(a, r, g, b));
    }

    public static int mix(float saturation, int colorA, int colorB) {
        if (saturation > 1)
            saturation = 1.0F;
        else if (saturation < 0)
            saturation = 0.F;
        float j = 1.0F - saturation;

        int a1 = alpha(colorA);
        int r1 = red(colorA);
        int g1 = green(colorA);
        int b1 = blue(colorA);

        int a2 = alpha(colorB);
        int r2 = red(colorB);
        int g2 = green(colorB);
        int b2 = blue(colorB);

        int a = (int)((a1 * j) + (a2 * saturation));
        int r = (int)((r1 * j) + (r2 * saturation));
        int g = (int)((g1 * j) + (g2 * saturation));
        int b = (int)((b1 * j) + (b2 * saturation));
        return to32BitColor(a, r, g, b);
    }

    public static int gradient(int colorFrom, int colorTo, float scale) {
        int r = (int) (red(colorFrom) * scale + red(colorTo) * (1.0F - scale));
        int g = (int) (green(colorFrom) * scale + green(colorTo) * (1.0F - scale));
        int b = (int) (blue(colorFrom) * scale + blue(colorTo) * (1.0F - scale));
        return toChatColor(r, g, b);
    }

    public static float[] rgbArray(int r, int g, int b) {
        return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
    }

    public static float[] rgbaArray(int r, int g, int b, int a) {
        return new float[]{r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F};
    }

    public static float[] decimalArray(int color) {
        int r = red(color);
        int g = green(color);
        int b = blue(color);
        return rgbArray(r, g, b);
    }
}
