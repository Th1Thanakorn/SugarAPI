package com.thana.sugarapi.common.api.annotations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public interface DevelopmentChecker {

    static boolean found() {
        return FMLEnvironment.dist != Dist.CLIENT;
    }
}
