package com.thana.sugarapi.common.core;

import com.thana.sugarapi.client.event.handler.ClientEventHandler;
import com.thana.sugarapi.client.event.handler.ConfigClientEventHandler;
import com.thana.sugarapi.client.gui.screen.SugarSettingsScreen;
import com.thana.sugarapi.client.keys.ModKeys;
import com.thana.sugarapi.common.api.oid.OIDUtil;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.SimpleLogger;
import com.thana.sugarapi.common.utils.VersionChecker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.stream.Stream;

@Mod(SugarAPI.MOD_ID)
public class SugarAPI {

    public static final SimpleLogger LOGGER = new SimpleLogger("SugarAPI");
    public static final String MOD_ID = "sugarapi";
    public static final String MOD_VERSION = "2.3.1";
    public static final String MACRO_PACKAGE = "com.thana.sugarapi.common.macro";

    public SugarAPI() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPreInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.register(this);

        JsonConfig.defaultCreatePath();
        SugarAPIClientConfigBuilder.create();
        SugarSettingsScreen.putConfig(SugarAPI.MOD_ID, "SugarAPI");
        OIDUtil.scanPackage(SugarAPI.MACRO_PACKAGE);
        VersionChecker.tryCheck(SugarAPI.MOD_ID, SugarAPI.MOD_VERSION);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.registerKeys();
            this.clientEvent();
        }
        else {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLServerSetup);
        }
    }

    public void onPreInit(final FMLCommonSetupEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
        Stream<InterModComms.IMCMessage> messages = event.getIMCStream();
        for (InterModComms.IMCMessage message : messages.toList()) {
            SugarSettingsScreen.putConfig(message.method(), (String) message.messageSupplier().get());
        }
    }

    public void onFMLClientSetup(final FMLClientSetupEvent event) {
        this.clientEvent();
    }

    public void onFMLServerSetup(final FMLDedicatedServerSetupEvent event) {
    }

    private void clientEvent() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ConfigClientEventHandler());
    }

    private void registerKeys() {
        ModKeys.init();
    }
}
