package com.thana.sugarapi.common.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StringEditor {

    // TODO: Renew Component Cutting
    public static MutableComponent cut(Component text, int length) {
        return (MutableComponent) text;
    }
}
