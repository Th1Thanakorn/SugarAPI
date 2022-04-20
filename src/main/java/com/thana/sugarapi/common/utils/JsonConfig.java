package com.thana.sugarapi.common.utils;

import com.google.gson.*;
import com.thana.sugarapi.common.core.SugarAPI;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class JsonConfig {

    private static final SimpleLogger LOGGER = new SimpleLogger("JsonConfig");
    public static final String DIR_CLIENT = "./config/sugarapi/client/";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String modid;
    private final String modVersion;
    private final HashMap<String, Object> config = new HashMap<>();

    public JsonConfig(String modid, String modVersion) {
        this.modid = modid;
        this.modVersion = modVersion;
    }

    public void put(String name, int i) {
        this.config.put(name, i);
    }

    public void put(String name, boolean bool) {
        this.config.put(name, bool);
    }

    public void put(String name, long l) {
        this.config.put(name, l);
    }

    public void put(String name, short s) {
        this.config.put(name, s);
    }

    public void put(String name, byte b) {
        this.config.put(name, b);
    }

    public void put(String name, char c) {
        this.config.put(name, c);
    }

    public void put(String name, double d) {
        this.config.put(name, d);
    }

    public void put(String name, float f) {
        this.config.put(name, f);
    }

    public void put(String name, String value) {
        this.config.put(name, value);
    }

    public void put(String name, JsonElement element) {
        this.config.put(name, element);
    }

    public void put(String name, int i, int min, int max) {
        JsonObject object = new JsonObject();
        object.addProperty("isPrimitive", true);
        object.addProperty("type", "int");
        object.addProperty("value", i);
        object.addProperty("min", min);
        object.addProperty("max", max);
        this.config.put(name, object);
    }

    public void put(String name, double i, double min, double max) {
        JsonObject object = new JsonObject();
        object.addProperty("isPrimitive", true);
        object.addProperty("type", "double");
        object.addProperty("value", i);
        object.addProperty("min", min);
        object.addProperty("max", max);
        this.config.put(name, object);
    }

    public void put(String name, float i, float min, float max) {
        JsonObject object = new JsonObject();
        object.addProperty("isPrimitive", true);
        object.addProperty("type", "float");
        object.addProperty("value", i);
        object.addProperty("min", min);
        object.addProperty("max", max);
        this.config.put(name, object);
    }

    public void put(String name, short i, short min, short max) {
        JsonObject object = new JsonObject();
        object.addProperty("isPrimitive", true);
        object.addProperty("type", "short");
        object.addProperty("value", i);
        object.addProperty("min", min);
        object.addProperty("max", max);
        this.config.put(name, object);
    }

    public void put(String name, long i, long min, long max) {
        JsonObject object = new JsonObject();
        object.addProperty("isPrimitive", true);
        object.addProperty("type", "long");
        object.addProperty("value", i);
        object.addProperty("min", min);
        object.addProperty("max", max);
        this.config.put(name, object);
    }

    public void putInLang(HashMap<String, String> langMap) {
        JsonObject object = new JsonObject();
        for (String key : langMap.keySet()) {
            String translation = langMap.get(key);
            object.addProperty(key, translation);
        }
        this.config.put("lang", object);
    }

    public void createConfigClient() {
        File file = new File(DIR_CLIENT + String.format("%s.json", this.modid));
        JsonObject oldConfig = JsonConfig.readClient(this.modid);
        try {
            if (file.createNewFile()) LOGGER.debug("Created config file for modid: " + this.modid);
            FileWriter writer = new FileWriter(file);
            writer.write(this.createJson(oldConfig));
            writer.close();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static JsonObject readClient(String modid) {
        JsonObject object = new JsonObject();
        File dir = new File(DIR_CLIENT);
        if (dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith(modid)) {
                    StringBuilder builder = new StringBuilder();
                    try {
                        FileReader reader = new FileReader(file);
                        int c;
                        while ((c = reader.read()) != -1) {
                            builder.append((char) c);
                        }
                        return JsonParser.parseString(builder.toString()).getAsJsonObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return object;
    }

    public static JsonObject modifiableConfig(String modid) {
        JsonObject object = readClient(modid);
        object.remove("version");
        object.remove("lastConfigId");
        return object;
    }

    // This is the best way to create json, because this version of JsonObject is really sucks
    private String createJson(JsonObject oldConfig) {
        JsonObject object = new JsonObject();
        object.addProperty("version", this.modVersion);
        for (String key : this.config.keySet()) {
            Object obj = this.config.get(key);
            if (obj instanceof Number number) {
                object.add(key, oldConfig.has(key) ? oldConfig.get(key) : new JsonPrimitive(number));
            }
            else if (obj instanceof String value) {
                object.addProperty(key, oldConfig.has(key) ? oldConfig.get(key).getAsString() : value);
            }
            else if (obj instanceof Boolean bool) {
                object.addProperty(key, oldConfig.has(key) ? oldConfig.get(key).getAsBoolean() : bool);
            }
            else if (obj instanceof JsonElement element) {
                object.add(key, oldConfig.has(key) ? oldConfig.get(key) : element);
            }
            else if (obj instanceof Character character) {
                object.addProperty(key, oldConfig.has(key) ? oldConfig.get(key).getAsString() : character.toString());
            }
        }
        return GSON.toJson(object);
    }

    private JsonObject read() {
        return readClient(this.modid);
    }

    public boolean is(String key) {
        return read().get(key).getAsBoolean();
    }

    public int getInt(String key) {
        return read().get(key) instanceof JsonObject ? read().get(key).getAsJsonObject().get("value").getAsInt() : read().get(key).getAsInt();
    }

    public double getDouble(String key) {
        return read().get(key).getAsDouble();
    }

    public float getFloat(String key) {
        return read().get(key).getAsFloat();
    }

    public short getShort(String key) {
        return read().get(key).getAsShort();
    }

    public long getLong(String key) {
        return read().get(key).getAsLong();
    }

    public char getChar(String key) {
        return read().get(key).getAsString().charAt(0);
    }

    public JsonObject getJsonObject(String key) {
        return read().get(key).getAsJsonObject();
    }

    public String getTranslation(String key) {
        return read().get("lang").getAsJsonObject().get(key).getAsString();
    }

    public static void defaultCreatePath() {
        File dir = new File(DIR_CLIENT);
        if (dir.mkdirs()) LOGGER.debug("Create default directory of config path");
    }

    public static void updateConfig(String modid, JsonObject config) {
        try {
            FileWriter writer = new FileWriter(DIR_CLIENT + modid + ".json");
            writer.write(GSON.toJson(config));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void set(String modid, String key, int i) {
        JsonObject object = readClient(modid);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, int i, int min, int max) {
        JsonObject object = readClient(modid);
        JsonObject primitive = new JsonObject();
        primitive.addProperty("isPrimitive", true);
        primitive.addProperty("type", "int");
        primitive.addProperty("value", i);
        primitive.addProperty("min", min);
        primitive.addProperty("max", max);

        object.add(key, primitive);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, long i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, boolean i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, short i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, double i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, float i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, JsonObject i) {
        JsonObject object = readClient(modid);
        object.add(key, i);
        updateConfig(modid, object);
    }

    public static void set(String modid, String key, String i) {
        JsonObject object = readClient(modid);
        object.addProperty(key, i);
        updateConfig(modid, object);
    }

    public static String getLastOpenedId() {
        return JsonConfig.readClient(SugarAPI.MOD_ID).getAsJsonObject().get("lastConfigId").getAsString();
    }

    public String getModid() {
        return this.modid;
    }
}
