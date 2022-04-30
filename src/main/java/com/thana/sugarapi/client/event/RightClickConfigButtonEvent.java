package com.thana.sugarapi.client.event;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraftforge.eventbus.api.Event;

public class RightClickConfigButtonEvent extends Event {

    private final String modid;
    private final String key;
    private final AbstractWidget widget;

    public RightClickConfigButtonEvent(String modid, String key, AbstractWidget widget) {
        this.modid = modid;
        this.key = key;
        this.widget = widget;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
    }

    public String getKey() {
        return this.key;
    }

    public AbstractWidget getWidget() {
        return this.widget;
    }
}
