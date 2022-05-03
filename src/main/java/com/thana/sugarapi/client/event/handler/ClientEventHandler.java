package com.thana.sugarapi.client.event.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.gui.screen.SugarSettingsScreen;
import com.thana.sugarapi.client.gui.widget.button.SugarSettingsButton;
import com.thana.sugarapi.common.utils.StringEditor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEventHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final TextComponentTagVisitor visitor = new TextComponentTagVisitor("", 0);

    @SubscribeEvent
    public void onTooltipRender(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.hasTag() && itemStack.getTag() != null && ClientConfig.is("showNbt")) {
            if (InputConstants.isKeyDown(this.mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                int i = ClientConfig.getInt("nbtLength");
                event.getToolTip().remove(event.getToolTip().size() - 1);
                event.getToolTip().add(StringEditor.cut(this.visitor.visit(itemStack.getTag()), i, ChatFormatting.WHITE + "}"));
            }
            else {
                event.getToolTip().add(new TextComponent(ChatFormatting.DARK_GRAY + "Press " + ChatFormatting.GOLD + ChatFormatting.ITALIC + "LShift " + ChatFormatting.DARK_GRAY + "to view nbt"));
            }
        }
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.InitScreenEvent event) {
        if (event.getScreen() instanceof PauseScreen) {
            int height = this.mc.getWindow().getGuiScaledHeight();
            event.addListener(new SugarSettingsButton(8, height - 22, event.getScreen(), (button) -> this.mc.setScreen(new SugarSettingsScreen())));
        }
    }
}
