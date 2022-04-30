package com.thana.sugarapi.common.utils.config;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Widget;

import java.util.HashMap;

public abstract class AdjustableVariable<A extends Number> {

    private final HashMap<String, A> object = new HashMap<>();
    private final String type;

    public AdjustableVariable(String type) {
        this.makeConfig(this.object);
        this.type = type;
    }

    public abstract void makeConfig(HashMap<String, A> map);
    public abstract AdjustableVariable<A> deserialize(JsonObject config);
    public abstract boolean sameType(String type);
    public abstract Widget toWidget(JsonObject config, int x, int y, int width, int height);

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", this.type);
        for (String key : this.object.keySet()) {
            Number b = this.object.get(key);
            object.addProperty(key, b);
        }
        return object;
    }
}
