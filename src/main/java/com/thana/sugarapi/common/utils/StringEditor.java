package com.thana.sugarapi.common.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StringEditor {

    public static MutableComponent cut(Component text, int length) {
        return (MutableComponent) text;
    }

    public static String upperFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String lowerFirst(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }
}
