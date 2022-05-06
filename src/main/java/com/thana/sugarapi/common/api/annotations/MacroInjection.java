package com.thana.sugarapi.common.api.annotations;

import net.minecraftforge.event.TickEvent;

public interface MacroInjection {

    void createMacro() throws Exception;
    void activateMacro(TickEvent.ClientTickEvent event);

    @MacroListener void onLoad() throws Exception;
    @MacroListener void onExecuted() throws Exception;
}
