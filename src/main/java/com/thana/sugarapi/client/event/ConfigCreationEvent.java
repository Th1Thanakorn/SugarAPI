package com.thana.sugarapi.client.event;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ConfigCreationEvent extends Event {

    private final String key;
    private final JsonElement value;
    private final int x;
    private final int y;
    private AbstractWidget setting = null;

    public ConfigCreationEvent(String key, JsonElement value, int x, int y) {
        this.key = key;
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public String getKey() {
        return this.key;
    }

    public boolean is(String key) {
        return this.key.equals(key);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public JsonElement getValue() {
        return this.value;
    }

    public void setSettingWidget(AbstractWidget setting) {
        this.setting = setting;
    }

    public AbstractWidget getSetting() {
        return setting;
    }
}
