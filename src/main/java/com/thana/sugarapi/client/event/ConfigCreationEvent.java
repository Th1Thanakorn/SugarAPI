package com.thana.sugarapi.client.event;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ConfigCreationEvent extends Event {

    private final String modid;
    private final String key;
    private final JsonElement value;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private AbstractWidget setting = null;

    public ConfigCreationEvent(String modid, String key, JsonElement value, int x, int y, int width, int height) {
        this.modid = modid;
        this.key = key;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public String getModid() {
        return modid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
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
