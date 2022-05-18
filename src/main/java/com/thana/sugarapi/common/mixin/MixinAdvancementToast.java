package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.common.utils.ARGBHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(AdvancementToast.class)
public class MixinAdvancementToast {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");

    private final AdvancementToast that = (AdvancementToast) (Object) this;

    @Shadow @Final private Advancement advancement;
    @Shadow private boolean playedSound;

    /**
     * @author Thana
     * @reason Enhanced toast
     */
    @Overwrite
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toast, long delta) {
        DisplayInfo displayinfo = this.advancement.getDisplay();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        if (displayinfo != null && displayinfo.getFrame() == FrameType.CHALLENGE) {
            int colorFrom = ARGBHelper.toChatColor(130, 5, 150);
            int colorTo = ARGBHelper.toChatColor(207, 114, 222);
            int graded = ARGBHelper.increaseGreen(colorFrom, colorTo, 0.2F, delta);
            float[] color = ARGBHelper.decimalArray(graded);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0F);
        }
        else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        toast.blit(poseStack, 0, 0, 0, 0, this.that.width(), this.that.height());

        if (displayinfo != null) {
            // Default Rendering
            List<FormattedCharSequence> list = toast.getMinecraft().font.split(displayinfo.getTitle(), 125);
            int i = displayinfo.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
            if (list.size() == 1) {
                toast.getMinecraft().font.draw(poseStack, displayinfo.getFrame().getDisplayName(), 30.0F, 7.0F, i | -16777216);
                toast.getMinecraft().font.draw(poseStack, list.get(0), 30.0F, 18.0F, -1);
            }
            else {
                if (delta < 1500L) {
                    int k = Mth.floor(Mth.clamp((float) (1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    toast.getMinecraft().font.draw(poseStack, displayinfo.getFrame().getDisplayName(), 30.0F, 11.0F, i | k);
                }
                else {
                    int i1 = Mth.floor(Mth.clamp((float) (delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int l = this.that.height() / 2 - list.size() * 9 / 2;
                    for (FormattedCharSequence formattedcharsequence : list) {
                        toast.getMinecraft().font.draw(poseStack, formattedcharsequence, 30.0F, (float) l, 16777215 | i1);
                        l += 9;
                    }
                }
            }
            if (!this.playedSound && delta > 0L) {
                this.playedSound = true;
                if (displayinfo.getFrame() == FrameType.CHALLENGE) {
                    toast.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
                }
            }
            toast.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(displayinfo.getIcon(), 8, 8);
            return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        else {
            return Toast.Visibility.HIDE;
        }
    }
}
