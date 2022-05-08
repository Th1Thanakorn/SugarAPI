package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.thana.sugarapi.common.utils.thread.AsyncThread;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Mixin(HttpTexture.class)
public abstract class MixinHttpTexture extends SimpleTexture {

    @Shadow private boolean uploaded;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Nullable private CompletableFuture<?> future;
    @Shadow @Final @Nullable private File file;
    @Shadow @Final private String urlString;

    @Shadow protected abstract void loadCallback(NativeImage image);
    @Shadow @Nullable protected abstract NativeImage load(InputStream stream);

    public MixinHttpTexture() {
        super(null);
    }

    /**
     * @author Thana
     * @reason Performance Tuning
     */
    @Overwrite
    public void load(@NotNull ResourceManager manager)  {
        AsyncThread.runAsync(() -> {
            try {
                Minecraft.getInstance().execute(() -> {
                    if (!this.uploaded) {
                        try {
                            super.load(manager);
                        } catch (IOException ioexception) {
                            LOGGER.warn("Failed to load texture: {}", this.location, ioexception);
                        }
                        this.uploaded = true;
                    }
                });
                if (this.future == null) {
                    NativeImage nativeimage = null;
                    if (this.file != null && this.file.isFile()) {
                        LOGGER.debug("Loading http texture from local cache ({})", this.file);
                        FileInputStream fileinputstream = new FileInputStream(this.file);
                        nativeimage = this.load(fileinputstream);
                    }
                    if (nativeimage != null) {
                        this.loadCallback(nativeimage);
                    } else {
                        this.future = CompletableFuture.runAsync(() -> {
                            HttpURLConnection httpurlconnection = null;
                            LOGGER.debug("Downloading http texture from {} to {}", this.urlString, this.file);
                            try {
                                httpurlconnection = (HttpURLConnection) (new URL(this.urlString)).openConnection(Minecraft.getInstance().getProxy());
                                httpurlconnection.setDoInput(true);
                                httpurlconnection.setDoOutput(false);
                                httpurlconnection.connect();
                                if (httpurlconnection.getResponseCode() / 100 == 2) {
                                    InputStream inputstream;
                                    if (this.file != null) {
                                        FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), this.file);
                                        inputstream = new FileInputStream(this.file);
                                    } else {
                                        inputstream = httpurlconnection.getInputStream();
                                    }

                                    Minecraft.getInstance().execute(() -> {
                                        NativeImage nativeimage1 = this.load(inputstream);
                                        if (nativeimage1 != null) {
                                            this.loadCallback(nativeimage1);
                                        }

                                    });
                                }
                            } catch (Exception exception) {
                                LOGGER.error("Couldn't download http texture", exception);
                            } finally {
                                if (httpurlconnection != null) {
                                    httpurlconnection.disconnect();
                                }
                            }
                        }, Util.backgroundExecutor());
                    }
                }
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }
}
