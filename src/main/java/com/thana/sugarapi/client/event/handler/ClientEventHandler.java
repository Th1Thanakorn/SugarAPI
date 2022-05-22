package com.thana.sugarapi.client.event.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.gui.screen.CommandEditorScreen;
import com.thana.sugarapi.client.gui.screen.SugarSettingsScreen;
import com.thana.sugarapi.client.gui.widget.button.ItemButton;
import com.thana.sugarapi.client.gui.widget.button.SugarSettingsButton;
import com.thana.sugarapi.common.api.annotations.ModHolder;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.utils.StringEditor;
import com.thana.sugarapi.common.utils.TextWrapper;
import com.thana.sugarapi.common.utils.VersionChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@ModHolder(SugarAPI.MOD_ID)
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
        int width = this.mc.getWindow().getGuiScaledWidth();
        int height = this.mc.getWindow().getGuiScaledHeight();
        if (event.getScreen() instanceof PauseScreen) {
            event.addListener(new SugarSettingsButton(8, height - 22, event.getScreen(), (button) -> this.mc.setScreen(new SugarSettingsScreen())));
        }
        else if (event.getScreen() instanceof ChatScreen) {
            event.addListener(new ItemButton(width - 26, (int) (height - 34 - this.mc.options.chatHeightFocused), TextComponent.EMPTY, new ItemStack(Items.PAPER), (button) -> this.mc.setScreen(new CommandEditorScreen())));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (this.mc.player != null && event.getEntity() == this.mc.player && VersionChecker.hasNewerVersion()) {
            String version = VersionChecker.getLatestVersion();
            this.mc.player.sendMessage(TextWrapper.wrapped("Found newer version for SugarAPI: " + ChatFormatting.RED + ChatFormatting.UNDERLINE + version), Util.NIL_UUID);
        }
    }

    @SubscribeEvent
    public void onMouseClicked(InputEvent.MouseInputEvent event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && event.getAction() == GLFW.GLFW_PRESS && this.mc.crosshairPickEntity != null && this.mc.crosshairPickEntity instanceof RemotePlayer remotePlayer && this.mc.player != null && this.mc.player.isShiftKeyDown() && ClientConfig.is("enablePlayerViewer")) {
            this.mc.player.sendMessage(this.visitor.visit(remotePlayer.serializeNBT()), Util.NIL_UUID);
        }
    }
}
