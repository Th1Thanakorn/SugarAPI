package com.thana.sugarapi.client.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;

public class SugarSettingsButton extends Button {

    private final TextComponent title = new TextComponent("SugarAPI Settings");
    private final Screen screen;
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public static final ResourceLocation TEXTURES = new ResourceLocation("sugarapi:textures/gui/button/sugarapi_template.png");

    public SugarSettingsButton(int x, int y, Screen screen, OnPress onPress) {
        super(x, y, 18, 18, TextComponent.EMPTY, onPress);
        this.screen = screen;
    }

    @Override
    public void renderButton(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0, TEXTURES);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Gui.blit(poseStack, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 18, 18);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.SUGAR), this.x + 1, this.y + 1);
        if (this.isHovered && this.active) {
            this.screen.renderTooltip(poseStack, this.title, mouseX, mouseY);
        }
    }
}
