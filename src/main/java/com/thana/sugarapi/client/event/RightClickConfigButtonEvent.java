package com.thana.sugarapi.client.event;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.sounds.SoundManager;
import net.minecraftforge.eventbus.api.Event;

public class RightClickConfigButtonEvent extends Event {

    private final String modid;
    private final String key;
    private final AbstractWidget widget;
    private final SoundManager manager;

    public RightClickConfigButtonEvent(String modid, String key, AbstractWidget widget, SoundManager manager) {
        this.modid = modid;
        this.key = key;
        this.widget = widget;
        this.manager = manager;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
    }

    public String getKey() {
        return this.key;
    }

    public String getModid() {
        return this.modid;
    }

    public AbstractWidget getWidget() {
        return this.widget;
    }

    public void playDownSound() {
        this.widget.playDownSound(this.manager);
    }
}
