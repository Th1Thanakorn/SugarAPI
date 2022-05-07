package com.thana.sugarapi.common.macro.type;

import com.thana.sugarapi.common.api.annotations.Legacy;
import com.thana.sugarapi.common.macro.MacroEngine;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

// *****************************************************
// I'm trying to recreate this method,
// but so bad that the keybinds doesn't work properly
// as the same as the last version.
//
// Author: HTMLChannel
// *****************************************************
@Legacy(old = true)
public class KeyPressMacro {

    private static final List<KeyMapping> hold = new ArrayList<>();

    public static void press(String key, int delay)
    {
        MacroEngine.validDelay(delay);
        for (KeyMapping key1 : Minecraft.getInstance().options.keyMappings)
        {
            if (key1.getName().equals(key) && Minecraft.getInstance().player != null
                    && (delay == 0 || Minecraft.getInstance().player.tickCount % delay == 0))
            {
                hold.forEach((keyx) -> KeyMapping.set(keyx.getKey(), false));
                hold.clear();

                KeyMapping.set(key1.getKey(), true);

                hold.add(key1);
            }
        }
    }
}
