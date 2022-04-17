package com.thana.sugarapi.client.gui.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SugarSettingsButton extends Button {

    private int tooltipTime = 0;
    private final TextComponent title = new TextComponent("SugarAPI");
    private final Screen screen;

    public static final ResourceLocation TEXTURES = new ResourceLocation("sugarapi:textures/gui/button/sugarapi_template.png");

    public SugarSettingsButton(int x, int y, Screen screen, OnPress onPress) {
        super(x, y, 18, 18, TextComponent.EMPTY, onPress);
        this.screen = screen;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, TEXTURES);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Gui.blit(poseStack, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 18, 18);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(new ItemStack(Items.SUGAR), this.x + 1, this.y + 1);
        if (this.isHovered && this.active) {
            poseStack.pushPose();
            float scaleAll = this.tooltipTime / 16.0F;
            poseStack.scale(scaleAll, scaleAll, scaleAll);
            this.screen.renderComponentTooltip(poseStack, List.of(this.title), (int) (mouseX * (1 / scaleAll)), (int) (mouseY * (1 / scaleAll)));
            poseStack.popPose();
            if (this.tooltipTime < 16) {
                this.tooltipTime += 2;
            }
        }
        else {
            this.tooltipTime = 0;
        }
    }
}
