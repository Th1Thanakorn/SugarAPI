package com.thana.sugarapi.common.macro.type;

import com.thana.sugarapi.common.macro.MacroEngine;
import net.minecraft.client.Minecraft;

// Author: Thana
public class MessagingMacro {

    public static void sendChat(String text, int delay) {
        MacroEngine.validDelay(delay);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && (delay == 0 || mc.player.tickCount % delay == 0)) {
            mc.player.chat(text);
        }
    }
}
