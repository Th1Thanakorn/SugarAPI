package com.thana.sugarapi.common.macro;

import com.mojang.logging.LogUtils;
import com.thana.sugarapi.common.api.annotations.MacroHolder;
import com.thana.sugarapi.common.api.annotations.MacroInjection;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MacroEngine {

    private static final List<String> interrupted = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void importNew(Object macro) throws Exception {
        if (macro instanceof MacroInjection i) {
            i.createMacro();
        }
        MinecraftForge.EVENT_BUS.register(macro);
        LOGGER.debug("Imported macro for modid: " + macro.getClass().getAnnotation(MacroHolder.class).value());
    }

    public static void onLoad(MacroInjection macro) throws Exception {
        macro.onLoad();
    }

    public static void onExecuted(MacroInjection macro) {
        try {
            macro.onExecuted();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void interrupt(MacroInjection macro) {
        interrupted.add(macro.getClass().getAnnotation(MacroHolder.class).value());
    }

    public static boolean interrupted(String modid) {
        return interrupted.contains(modid);
    }

    public static boolean interrupted(MacroInjection macro) {
        return interrupted.contains(macro.getClass().getAnnotation(MacroHolder.class).value());
    }
}
