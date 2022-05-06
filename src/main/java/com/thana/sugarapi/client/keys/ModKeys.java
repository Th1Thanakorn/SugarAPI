package com.thana.sugarapi.client.keys;

import com.thana.sugarapi.common.core.SugarAPI;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final ModKeyMapping KEY_REFRESH_MACRO = new ModKeyMapping("refresh_macro", SugarAPI.MOD_ID, GLFW.GLFW_KEY_F9);

    public static void init() {
        ClientRegistry.registerKeyBinding(KEY_REFRESH_MACRO);
    }
}
