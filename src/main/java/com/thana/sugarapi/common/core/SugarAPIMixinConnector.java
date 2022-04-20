package com.thana.sugarapi.common.core;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class SugarAPIMixinConnector implements IMixinConnector {

    public void connect() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.sugarapi.json");
    }
}
