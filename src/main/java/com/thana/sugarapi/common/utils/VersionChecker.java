package com.thana.sugarapi.common.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thana.sugarapi.common.api.annotations.ModHolder;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VersionChecker {

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final SimpleLogger LOGGER = new SimpleLogger("VersionChecker");
    private static final String URL = "https://drive.google.com/uc?export=download&id=1hF0BDaMqurfw9sv64S5bekfxJ5jqjPT6";
    private static final HashMap<String, String> NEWER_VERSION = new HashMap<>();

    /**
     * This function needs to be call in the mod constructor.
     * And it will collect the information about the latest mod and saving in.
     * You can also call it by using the methods below {@link #getLatestVersion()}
     * If you want to call it, please annotate the caller class using @ModHolder
     *
     * @param modid Currently checking modid
     * @param version Currently checking mod version
     */
    public static void tryCheck(String modid, String version) {
        try {
            URL url = new URL(URL);
            InputStream stream = url.openConnection().getInputStream();
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = stream.read()) != -1) {
                builder.append((char) c);
            }
            JsonObject object = JsonParser.parseString(builder.toString()).getAsJsonObject();
            JsonObject modList = object.getAsJsonObject("mods");
            if (modList.has(modid)) {
                JsonArray array = modList.getAsJsonArray(modid);
                List<String> list = new ArrayList<>();
                for (JsonElement element : array) {
                    list.add(element.getAsString());
                }
                List<String> sortedList = list.stream().sorted(VersionChecker::compareVersion).toList();
                String latestVersion = sortedList.get(sortedList.size() - 1);
                if (!latestVersion.equals(version)) {
                    LOGGER.warning("Found newer version of modid " + modid + ", at version: " + latestVersion);
                    NEWER_VERSION.put(modid, latestVersion);
                }
            }
            else {
                LOGGER.error(String.format("Modid of {%s} could not found in version checker!", modid));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static int compareVersion(String o1, String o2) {
        String[] s1 = o1.split("\\.");
        String[] s2 = o2.split("\\.");
        if (Integer.parseInt(s1[0]) < Integer.parseInt(s2[0])) {
            return Integer.compare(0, 1);
        }
        else {
            if (Integer.parseInt(s1[1]) < Integer.parseInt(s2[1])) {
                return Integer.compare(0, 1);
            }
            else {
                if (Integer.parseInt(s1[2]) < Integer.parseInt(s2[2])) {
                    return Integer.compare(0, 1);
                }
            }
        }
        return Integer.compare(1, 0);
    }

    public static boolean hasNewerVersion() {
        Class<?> caller = WALKER.getCallerClass();
        if (caller.isAnnotationPresent(ModHolder.class)) {
            String modid = caller.getAnnotation(ModHolder.class).value();
            return NEWER_VERSION.containsKey(modid);
        }
        return false;
    }

    public static String getLatestVersion() {
        Class<?> caller = WALKER.getCallerClass();
        if (caller.isAnnotationPresent(ModHolder.class)) {
            String modid = caller.getAnnotation(ModHolder.class).value();
            return NEWER_VERSION.get(modid);
        }
        return "";
    }
}
