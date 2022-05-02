package com.thana.sugarapi.client.event;

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Cancelable
public class ConfigTooltipEvent extends Event {

    private final String modid;
    private final String key;
    private final ArrayList<Component> tooltip = new ArrayList<>();

    public ConfigTooltipEvent(String modid, String key) {
        this.modid = modid;
        this.key = key;
    }

    public boolean sameMod(String modid) {
        return this.modid.equals(modid);
    }

    public String getKey() {
        return key;
    }

    public void withTooltip(Component... tooltips) {
        this.tooltip.addAll(Arrays.stream(tooltips).toList());
        this.setCanceled(true);
    }

    public List<Component> getTooltip() {
        return tooltip;
    }
}
