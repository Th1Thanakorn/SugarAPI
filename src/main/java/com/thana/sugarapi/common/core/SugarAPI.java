package com.thana.sugarapi.common.core;

import com.thana.sugarapi.client.event.handler.ClientEventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mod(SugarAPI.MOD_ID)
public class SugarAPI {

    public static final Logger LOGGER = LogManager.getLogManager().getLogger("SugarAPI");
    public static final String MOD_ID = "sugarapi";

    public SugarAPI() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPreInit);
        MinecraftForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.clientEvent();
        }
        else {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLServerSetup);
        }
    }

    public void onPreInit(final FMLCommonSetupEvent event) {
    }

    public void onFMLClientSetup(final FMLClientSetupEvent event) {
        this.clientEvent();
    }

    public void onFMLServerSetup(final FMLDedicatedServerSetupEvent event) {
    }

    private void clientEvent() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
