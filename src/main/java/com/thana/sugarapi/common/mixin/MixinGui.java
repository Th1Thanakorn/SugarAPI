package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void displayScoreboard(PoseStack poseStack, Objective objective, CallbackInfo info) {
        if (ClientConfig.is("hideScoreboard")) {
            info.cancel();
        }
    }
}
