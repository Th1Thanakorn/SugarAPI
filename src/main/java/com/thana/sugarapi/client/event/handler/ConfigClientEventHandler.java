package com.thana.sugarapi.client.event.handler;

import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.config.handler.MouseScroll;
import com.thana.sugarapi.client.event.ConfigCreationEvent;
import com.thana.sugarapi.client.event.ConfigNameCreationEvent;
import com.thana.sugarapi.client.event.RightClickConfigButtonEvent;
import com.thana.sugarapi.client.event.SpecialJobEvent;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.StringEditor;
import com.thana.sugarapi.common.utils.TextWrapper;
import com.thana.sugarapi.common.utils.config.ChatFormattingSettings;
import com.thana.sugarapi.common.utils.config.EnumSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;

public class ConfigClientEventHandler {

    public static final ChatFormatting[] onlyColor = Arrays.stream(ChatFormatting.values()).filter((color) -> color != ChatFormatting.ITALIC && color != ChatFormatting.BOLD && color != ChatFormatting.RESET && color != ChatFormatting.UNDERLINE && color != ChatFormatting.OBFUSCATED && color != ChatFormatting.STRIKETHROUGH).toList().toArray(ChatFormatting[]::new);

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onConfigCreation(ConfigCreationEvent event) {
        String key = event.getKey();
        int x = event.getX();
        int y = event.getY();
        int width = event.getWidth();
        int height = event.getHeight();
        if (event.sameMod(SugarAPI.MOD_ID)) {
            if (event.is("headerColor")) {
                Button button = ChatFormattingSettings.create(x, y, width, height, key, ClientConfig.getString(key), event.getModid());
                if (button != null) {
                    event.setSettingWidget(button);
                    event.setCanceled(true);
                }
            }
            else if (event.is("scrollDelta")) {
                Button button = new EnumSettings<>(SugarAPI.MOD_ID, key, MouseScroll.fromName(ClientConfig.getString(key)), x, y, width, height).create(ChatFormatting.GOLD);
                event.setSettingWidget(button);
                event.setCanceled(true);
            }
            else if (event.is("multiKeys")) {
                event.setStop(true);
            }
        }
    }

    @SubscribeEvent
    public void onNameCreation(ConfigNameCreationEvent event) {
        if (event.sameMod(SugarAPI.MOD_ID)) {
            switch (event.getKey()) {
                case "nbtLength" -> event.setReturn("NBT Length");
                case "showNbt" -> event.setReturn("Show NBT");
                default -> event.setReturn(this.camelCaseChange(event.getKey()));
            }
            if (event.getKey().equals("multiKeys")) {
                event.setRender(false);
            }
        }
        else {
            event.setReturn(this.camelCaseChange(event.getKey()));
        }
    }

    @SubscribeEvent
    public void onRightClickConfig(RightClickConfigButtonEvent event) {
        String key = event.getKey();
        AbstractWidget widget = event.getWidget();
        if (event.sameMod(SugarAPI.MOD_ID)) {
            if (key.equals("headerColor")) {
                String colorString = ClientConfig.getString(key);
                ChatFormatting formatting = ChatFormatting.getByName(colorString);
                if (formatting != null) {
                    ChatFormatting previous = onlyColor[(onlyColor.length + (formatting.ordinal() - 1)) % onlyColor.length];
                    widget.setMessage(TextWrapper.wrapped(this.formatName(previous.getName()), previous));
                    JsonConfig.set(SugarAPI.MOD_ID, key, previous.getName());
                    event.playDownSound();
                }
            }
            else if (key.equals("scrollDelta")) {
                EnumSettings.reverseClick(event, MouseScroll.NORMAL, ChatFormatting.GOLD);
            }
        }
    }

    @SubscribeEvent
    public void addCustomJob(SpecialJobEvent event) {
        String key = event.getKey();
        if (event.sameMod(SugarAPI.MOD_ID)) {
            if (key.equals("flyingSpeed")) {
                event.setJob(() -> {
                    if (this.mc.player != null) {
                        this.mc.player.getAbilities().setFlyingSpeed(0.05F * (float) ClientConfig.getDouble(key));
                    }
                });
            }
        }
    }

    private String formatName(String name) {
        return name.toUpperCase().replaceAll("_", " ");
    }

    private String camelCaseChange(String name) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(" ");
            }
            builder.append(c);
        }
        String[] sets = builder.toString().split(" ");
        StringBuilder retBuilder = new StringBuilder();
        for (int i = 0; i < sets.length; i++) {
            String s = sets[i];
            retBuilder.append(StringEditor.upperFirst(s));
            if (i != sets.length - 1) {
                retBuilder.append(" ");
            }
        }
        return retBuilder.toString();
    }
}
