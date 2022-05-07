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

    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static String fastRgbToHex(int r, int g, int b) {
        return "#" + Integer.toHexString(toChatColor(r, g, b));
    }

    public static String fastArgbToHex(int a, int r, int g, int b) {
        return "#" + Integer.toHexString(toChatColor(a, r, g, b));
    }

    public static int blend(int colorA, int colorB, float i) {
        if (i > 1)
            i = 1.0F;
        else if (i < 0)
            i = 0.0F;

        float j = 1F - i;
        int k1 = (colorA >> 24 & 0xff);
        int k2 = ((colorA & 0xff0000) >> 16);
        int k3 = ((colorA & 0xff00) >> 8);
        int k4 = (colorA & 0xff);

        int l1 = (colorB >> 24 & 0xff);
        int l2 = ((colorB & 0xff0000) >> 16);
        int l3 = ((colorB & 0xff00) >> 8);
        int l4 = (colorB & 0xff);
        int a = (int) (k1 * j + (l1 * i));
        int r = (int) (k2 * j + (l2 * i));
        int g = (int) (k3 * j + (l3 * i));
        int b = (int) (k4 * j + (l4 * i));
        return to32BitColor(a, r, g, b);
    }
}
