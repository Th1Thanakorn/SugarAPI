package com.thana.sugarapi.client.keys;

import net.minecraft.client.KeyMapping;

public class ModKeyMapping extends KeyMapping {

    public ModKeyMapping(String name, String modid, int key) {
        super("key." + name + ".desc", key, "key.categories." + modid);
    }
}
