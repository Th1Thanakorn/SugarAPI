package com.thana.sugarapi.common.api.annotations;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public interface MacroInjection {

    void createMacro() throws Exception;
    @SubscribeEvent void activateMacro(TickEvent.ClientTickEvent event);
    @MacroListener void onLoad() throws Exception;
    @MacroListener void onExecuted() throws Exception;
}
