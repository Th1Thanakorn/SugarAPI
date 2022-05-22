package com.thana.sugarapi.client.event;

import net.minecraftforge.eventbus.api.Event;

public class ConfigChangeEvent extends Event {

    private final String modid;
    private final String key;

    public ConfigChangeEvent(String modid, String key) {
        this.modid = modid;
        this.key = key;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
    }

    public String getKey() {
        return key;
    }
}
