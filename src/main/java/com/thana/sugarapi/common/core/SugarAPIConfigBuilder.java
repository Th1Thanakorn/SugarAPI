package com.thana.sugarapi.common.core;

import com.thana.sugarapi.common.utils.JsonConfig;

public class SugarAPIConfigBuilder {

    public static final JsonConfig CONFIG = new JsonConfig(SugarAPI.MOD_ID, SugarAPI.MOD_VERSION);

    public static void create() {
        CONFIG.put("showNbt", true);
        CONFIG.put("nbtLength", 32767, 0, 32767);
        CONFIG.createConfigClient();
    }
}
