package com.thana.sugarapi.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ConfigNameCreationEvent extends Event {

    private final String key;
    private final String modid;
    private String ret;

    public ConfigNameCreationEvent(String modid, String key) {
        this.modid = modid;
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setReturn(String ret) {
        this.ret = ret;
        this.setCanceled(true);
    }

    public String getReturn() {
        return this.ret;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
    }
}
