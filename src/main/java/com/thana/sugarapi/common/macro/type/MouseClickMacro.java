package com.thana.sugarapi.common.macro.type;

import com.thana.sugarapi.common.macro.MacroEngine;
import com.thana.sugarapi.common.utils.SimpleLogger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

// Author: HTMLChannel
// Fixed by: Thana
public class MouseClickMacro {

    private final static SimpleLogger LOGGER = new SimpleLogger("MouseClickMacro");
    private final static HashMap<String, Integer> MAP = new HashMap<>() {{
        this.put("left", GLFW.GLFW_MOUSE_BUTTON_LEFT);
        this.put("right", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        this.put("middle", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
    }};

    // simulate mouse click
    public static void click(String mouseButton, int delay, double mx, double my) {
        MacroEngine.validDelay(delay);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && (delay == 0 || mc.player.tickCount % delay == 0)) {
            if (mc.screen != null) {
                if (!MAP.containsKey(mouseButton)) {
                    LOGGER.error("Could not click mouse button: " + mouseButton);
                    return;
                }
                Screen screen = mc.screen;
                screen.mouseClicked(mx, my, MAP.get(mouseButton));
            }
            switch (mouseButton) {
                case "left" -> {
                    KeyMapping key = mc.options.keyAttack;
                    KeyMapping.click(key.getKey());
                }
                case "right" -> {
                    KeyMapping key = mc.options.keyUse;
                    KeyMapping.click(key.getKey());
                }
                case "middle" -> {
                    KeyMapping key = mc.options.keyPickItem;
                    KeyMapping.click(key.getKey());
                }
                default -> LOGGER.error("Could not click mouse button: " + mouseButton);
            }
        }
    }

    @SuppressWarnings("unused")
    public static void putMouseType(String type, int button) {
        MAP.put(type, button);
    }
}
