package com.thana.sugarapi.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ScrollingTabButton extends Button {

    private static final ResourceLocation TEXTURES = new ResourceLocation("sugarapi:textures/gui/scrolling_tab.png");

    public int drawWidth;
    public float drawHeight;

    public ScrollingTabButton(int x, int y, int drawWidth, int drawHeight, OnPress onSlide) {
        super(x, y, drawWidth, drawHeight, TextComponent.EMPTY, onSlide);
        this.drawWidth = drawWidth;
        this.drawHeight = drawHeight;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, TEXTURES);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Gui.blit(poseStack, this.x, this.y, this.getBlitOffset(), this.drawWidth, this.drawHeight, this.drawWidth, (int) this.drawHeight, 4, 16);
    }

    public boolean isHovered() {
        return this.isHovered;
    }
}
