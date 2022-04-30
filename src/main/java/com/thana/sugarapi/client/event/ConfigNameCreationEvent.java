package com.thana.sugarapi.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ConfigNameCreationEvent extends Event {

    private final String name;
    private final String modid;
    private String ret;

    public ConfigNameCreationEvent(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    public String getName() {
        return name;
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
