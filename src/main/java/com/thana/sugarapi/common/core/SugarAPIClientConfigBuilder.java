package com.thana.sugarapi.common.core;

import com.thana.sugarapi.client.config.handler.MouseScroll;
import com.thana.sugarapi.common.utils.JsonConfig;

import java.util.HashMap;

public class SugarAPIClientConfigBuilder {

    public static final JsonConfig CONFIG = new JsonConfig(SugarAPI.MOD_ID, SugarAPI.MOD_VERSION);

    public static void create() {
        CONFIG.put("lastConfigId", "");
        CONFIG.put("showNbt", true);
        CONFIG.put("nbtLength", 32767, 0, 32767);
        CONFIG.put("blindnessSprint", false);
        CONFIG.put("renderBlindness", true);
        CONFIG.put("flyingSpeed", 1.0D, 1.0D, 4.0D);
        CONFIG.put("hideScoreboard", false);
        CONFIG.put("formatDebugOverlay", true);
        CONFIG.put("headerColor", "aqua");
        CONFIG.put("scrollDelta", MouseScroll.SLOW.toString());
        CONFIG.put("showPingText", false);
        CONFIG.put("enablePlayerViewer", true);
        CONFIG.putList("multiKeys", new HashMap<>());

        CONFIG.createConfigClient();
        CONFIG.createKeyLoggerClient();
    }
}
