package com.thana.sugarapi.common.utils;

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
}
