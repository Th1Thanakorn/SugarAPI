package com.thana.sugarapi.common.utils.config;

import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;

import java.util.Arrays;

public final class ChatFormattingSettings {

    public static final ChatFormatting[] onlyColor = Arrays.stream(ChatFormatting.values()).filter((color) -> color != ChatFormatting.ITALIC && color != ChatFormatting.BOLD && color != ChatFormatting.RESET && color != ChatFormatting.UNDERLINE && color != ChatFormatting.OBFUSCATED && color != ChatFormatting.STRIKETHROUGH).toList().toArray(ChatFormatting[]::new);

    public static Button create(int x, int y, int width, int height, String key, String color, String modid) {
        ChatFormatting formatting = ChatFormatting.getByName(color);
        if (formatting != null) {
            return new Button(x, y, width, height, TextWrapper.wrapped(formatName(formatting.getName()), formatting), (button) -> {
                ChatFormatting f = ChatFormatting.getByName(ClientConfig.getString(key));
                if (f != null) {
                    ChatFormatting next = onlyColor[(f.ordinal() + 1) % onlyColor.length];
                    button.setMessage(TextWrapper.wrapped(formatName(next.getName()), next));
                    JsonConfig.set(modid, key, next.getName());
                }
            });
        }
        return null;
    }

    private static String formatName(String name) {
        return name.toUpperCase().replaceAll("_", " ");
    }
}
