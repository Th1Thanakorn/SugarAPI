package com.thana.sugarapi.common.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class TextWrapper {

    public static Component wrapped(String text) {
        return wrapped(text, Style.EMPTY);
    }

    public static Component wrapped(String text, ChatFormatting... style) {
        return new TextComponent(text).withStyle(style);
    }

    public static Component wrapped(String text, Style style) {
        return new TextComponent(text).withStyle(style);
    }
}
