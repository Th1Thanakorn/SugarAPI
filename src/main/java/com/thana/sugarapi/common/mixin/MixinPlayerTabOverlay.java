package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.common.utils.FontColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerTabOverlay {

    private static final ResourceLocation UNICODE = new ResourceLocation("uniform");
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "renderPingIcon", at = @At("HEAD"), cancellable = true)
    public void renderPingIcon(PoseStack matrixStack, int guiX, int guiWidth, int guiY, PlayerInfo playerInfo, CallbackInfo info) {
        if (ClientConfig.is("showPingText")) {
            MutableComponent component = this.pingToText(playerInfo);
            this.mc.font.draw(matrixStack, component, guiX + guiWidth - 10, guiY, FontColor.WHITE);
            info.cancel();
        }
    }

    private MutableComponent pingToText(PlayerInfo info) {
        int ping = info.getLatency();
        int clampPing = Mth.clamp(ping, 0, 999);
        int color = Mth.hsvToRgb(Math.min(1.0F, (1.0F - this.scalePing(clampPing))) / 3.0F, 1.0F, 1.0F);
        MutableComponent component = new TextComponent(String.valueOf(clampPing));
        component.setStyle(component.getStyle().withColor(color).withFont(UNICODE));
        return component;
    }

    private float scalePing(int ping) {
        return ping / 999.0F;
    }
}
