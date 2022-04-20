package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {

    @Shadow protected int screenWidth;
    @Shadow protected int screenHeight;

    private static final ResourceLocation TEXTURES = new ResourceLocation("sugarapi:textures/gui/blindness_gui.png");

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBlindness(PoseStack poseStack, float partialTicks, CallbackInfo info) {
        Minecraft mc = Minecraft.getInstance();
        if (ClientConfig.is("renderBlindness") && mc.player != null) {
            if (mc.player.hasEffect(MobEffects.BLINDNESS)) {
                float alpha = 0.6F;
                RenderSystem.setShaderTexture(0, TEXTURES);
                poseStack.pushPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                Gui.blit(poseStack, 0, 0, this.screenWidth, this.screenHeight, 0, 0, 200, 150, 200, 150);
                poseStack.popPose();
            }
        }
    }
}
