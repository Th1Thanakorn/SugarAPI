package com.thana.sugarapi.common.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class StringEditor {

    // I know it's not possible right now to split text perfectly
    // Because the ways that Minecraft holds all the fonts are completely bad
    // They hold font as FormattedCharSequence which is not possible to our method
    // Written by: HTMLChannel
    public static MutableComponent cut(Component text, int length, String suffix) {
        if (text.getString().length() > length) {
            MutableComponent component = new TextComponent("");
            int i = 0;
            for (Component sibling : text.getSiblings()) {
                int l = sibling.getString().length();
                i += l;
                component.append(sibling).withStyle(sibling.getStyle());
                if (i >= length) {
                    break;
                }
            }
            component.append(ChatFormatting.GRAY + "...");
            component.append(suffix);
            return component;
        }
        else {
            return (MutableComponent) text;
        }
    }

    public static String upperFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String lowerFirst(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }
}
