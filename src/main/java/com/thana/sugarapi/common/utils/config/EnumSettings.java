package com.thana.sugarapi.common.utils.config;

import com.google.gson.JsonObject;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;

public record EnumSettings<V, E extends ConfigEnum<V>>(String modid, String key, E enumHolder, int x, int y, int width, int height) {

    public AbstractWidget create() {
        return new Button(this.x, this.y, this.width, this.height, TextWrapper.wrapped(this.enumHolder.save(this.getCurrent())), (button) -> {
            V value = this.getCurrent();
            V next = this.enumHolder.next(value);
            JsonConfig.set(this.modid, this.key, this.enumHolder.save(next));
        });
    }

    public V getCurrent() {
        JsonObject object = JsonConfig.modifiableConfig(this.modid);
        String enumString = object.get(key).getAsString();
        return this.enumHolder.parse(enumString);
    }
}
