package com.thana.sugarapi.common.data;

import com.google.gson.JsonObject;
import com.thana.sugarapi.common.utils.config.AdjustableVariable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.TextComponent;

import java.util.HashMap;

public class RGBConfig extends AdjustableVariable<Integer> {

    private static final String TYPE = "RGBA";
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    public RGBConfig(int r, int g, int b, int a) {
        super(TYPE);
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
    }

    @Override
    public void makeConfig(HashMap<String, Integer> map) {
        map.put("red", this.red);
        map.put("green", this.green);
        map.put("blue", this.blue);
        map.put("alpha", this.alpha);
    }

    @Override
    public RGBConfig deserialize(JsonObject config) {
        int red = config.get("red").getAsInt();
        int green = config.get("green").getAsInt();
        int blue = config.get("blue").getAsInt();
        int alpha = config.get("alpha").getAsInt();
        return new RGBConfig(red, green, blue, alpha);
    }

    @Override
    public boolean sameType(String type) {
        return type.equals(TYPE);
    }

    @Override
    public Widget toWidget(JsonObject config, int x, int y, int width, int height) {
        return new Button(x, y, width, height, new TextComponent("Simple Button"), (button) -> {});
    }
}
