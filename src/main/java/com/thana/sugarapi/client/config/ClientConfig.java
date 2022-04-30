package com.thana.sugarapi.client.config;

import com.thana.sugarapi.common.core.SugarAPIClientConfigBuilder;
import com.thana.sugarapi.common.utils.JsonConfig;

public class ClientConfig {

    private static final JsonConfig CONFIG = SugarAPIClientConfigBuilder.CONFIG;

    public static void read(String modid) {
        JsonConfig.readClient(modid);
    }

    public static String getModVersion(String modid) {
        return JsonConfig.readClient(modid).get("version").getAsString();
    }

    public static boolean is(String key) {
        return CONFIG.is(key);
    }

    public static int getInt(String key) {
        return CONFIG.getInt(key);
    }

    public static double getDouble(String key) {
        return CONFIG.getDouble(key);
    }

    public static String getString(String key) {
        return CONFIG.getString(key);
    }
}
