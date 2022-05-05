package com.thana.sugarapi.common.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.thana.sugarapi.client.gui.screen.NBTEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen<T extends AbstractContainerMenu> {

    @Shadow @Nullable public abstract Slot getSlotUnderMouse();
    @Shadow public abstract boolean mouseClicked(double mouseX, double mouseY, int button);

    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (InputConstants.isKeyDown(this.mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) && keyCode == GLFW.GLFW_KEY_X) {
            if (this.getSlotUnderMouse() != null && this.getSlotUnderMouse().hasItem() && this.getSlotUnderMouse().getItem().hasTag()) {
                this.mc.setScreen(new NBTEditorScreen(this.getSlotUnderMouse().getItem()));
            }
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"))
    public void mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && InputConstants.isKeyDown(this.mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            Slot slot = this.getSlotUnderMouse();
            if (slot != null) {
                this.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }
}
