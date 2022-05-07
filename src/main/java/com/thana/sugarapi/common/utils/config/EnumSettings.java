package com.thana.sugarapi.common.utils.config;

import com.google.gson.JsonObject;
import com.thana.sugarapi.client.event.RightClickConfigButtonEvent;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;

public record EnumSettings<E extends ConfigEnum<E>>(String modid, String key, E currentValue, int x, int y, int width, int height) {

    public Button create(ChatFormatting color) {
        return new Button(this.x, this.y, this.width, this.height, TextWrapper.wrapped(this.currentValue.save(), color), (button) -> {
            E value = this.getCurrent();
            E next = value.next();
            button.setMessage(TextWrapper.wrapped(next.save(), color));
            JsonConfig.set(this.modid, this.key, next.save());
        });
    }

    public E getCurrent() {
        JsonObject object = JsonConfig.modifiableConfig(this.modid);
        String enumString = object.get(this.key).getAsString();
        return this.currentValue.parse(enumString);
    }

    public static <F extends ConfigEnum<F>> F parse(F example, String modid, String key) {
        String enumString = JsonConfig.modifiableConfig(modid).get(key).getAsString();
        return example.parse(enumString);
    }

    public static <F extends ConfigEnum<F>> void reverseClick(RightClickConfigButtonEvent event, F example, ChatFormatting baseColor) {
        AbstractWidget widget = event.getWidget();
        String key = event.getKey();
        String modid = event.getModid();
        JsonObject object = JsonConfig.modifiableConfig(modid);
        String enumString = object.get(key).getAsString();
        F enumValue = example.parse(enumString);
        F previous = enumValue.previous();
        widget.setMessage(TextWrapper.wrapped(previous.save(), baseColor));
        JsonConfig.set(modid, key, previous.save());
        event.playDownSound();
    }
}
