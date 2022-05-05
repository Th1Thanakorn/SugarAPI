package com.thana.sugarapi.common.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class TextWrapper {

    public static MutableComponent wrapped(String text) {
        return wrapped(text, Style.EMPTY);
    }

    public static MutableComponent wrapped(String text, ChatFormatting... style) {
        return new TextComponent(text).withStyle(style);
    }

    public static MutableComponent wrapped(String text, Style style) {
        return new TextComponent(text).withStyle(style);
    }

    public static MutableComponent wrappedColor(String text, int color) {
        MutableComponent component = wrapped(text);
        return component.withStyle(component.getStyle().withColor(color));
    }
}
