package com.thana.sugarapi.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ItemButton extends Button {

    private static final ResourceLocation TEXTURES = new ResourceLocation("sugarapi:textures/gui/button/item_button.png");

    private final Minecraft mc = Minecraft.getInstance();
    private final ItemStack itemStack;

    private final Screen screen;

    public ItemButton(int x, int y, Component tooltip, ItemStack itemStack, OnPress onPress) {
        this(x, y, tooltip, itemStack, onPress, null);
    }

    public ItemButton(int x, int y, Component tooltip, ItemStack itemStack, OnPress onPress, Screen screen) {
        super(x, y, 18, 18, tooltip, onPress);
        this.itemStack = itemStack;
        this.screen = screen;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURES);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Gui.blit(poseStack, this.x, this.y, this.isHovered ? 18 : 0, 0, 18, 18, 36, 18);
        this.mc.getItemRenderer().renderAndDecorateItem(this.itemStack, this.x + 1, this.y + 1);
        if (this.isHovered && this.screen != null) {
            this.screen.renderTooltip(poseStack, this.getMessage(), mouseX, mouseY);
        }
    }
}
