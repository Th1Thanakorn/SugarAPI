package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.gui.widget.button.BooleanEditBox;
import com.thana.sugarapi.common.api.gui.ISuggestBox;
import com.thana.sugarapi.common.utils.FontColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

public class NBTEditorScreen extends Screen {

    private final Minecraft mc = Minecraft.getInstance();
    private final ItemStack stack;
    private final CompoundTag nbtHolder;
    private final HashMap<String, Integer> nbtType = new HashMap<>();

    private int textStartX;
    private int textStartY;
    private EditBox nbtBox;

    public NBTEditorScreen(ItemStack stack) {
        super(new TextComponent("NBT Editor"));
        this.stack = stack;
        this.nbtHolder = stack.getOrCreateTag();
    }

    @Override
    public void init() {
        this.mc.keyboardHandler.setSendRepeatsToGui(true);
        this.textStartX = this.width / 6 + this.width / 12;
        this.textStartY = this.height / 7 + 82;
        this.nbtBox = null;
        this.nbtType.clear();

        this.displayNBTSettings();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.fillGradient(poseStack, this.width / 6, this.height / 7, this.width * 5 / 6, this.height * 6 / 7, -1072689136, -804253680);
        Gui.drawCenteredString(poseStack, this.mc.font, this.title, this.width / 2, this.height / 7 + 10, FontColor.WHITE);
        this.mc.getItemRenderer().renderAndDecorateItem(this.stack, this.width / 2 - 9, this.height / 7 + 24);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // Draw Key
        for (int i = 0; i < this.nbtHolder.getAllKeys().size(); i++) {
            String key = this.nbtHolder.getAllKeys().stream().toList().get(i);
            this.font.drawShadow(poseStack, key, this.textStartX, this.textStartY + i * 24, FontColor.PURE_LIGHT_BLUE);
        }
   }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof ISuggestBox && listener instanceof EditBox box && box.isFocused()) {
                box.keyPressed(keyCode, scanCode, modifiers);
                return false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        this.mc.keyboardHandler.setSendRepeatsToGui(false);
        super.onClose();
    }

    private void displayNBTSettings() {
        Set<String> keys = this.nbtHolder.getAllKeys();
        int maxLength = 0;
        for (String key : keys) {
            maxLength = Math.max(maxLength, this.font.width(key));
            this.nbtType.put(key, (int) this.nbtHolder.getTagType(key));
        }
        maxLength += 6;

        // Common Editor
        this.nbtBox = new EditBox(this.font, this.width / 3, this.height / 7 + 50, this.width / 3, 20, new TextComponent(""));
        this.nbtBox.setEditable(true);
        this.nbtBox.setBordered(false);
        this.nbtBox.setCanLoseFocus(true);
        this.nbtBox.setMaxLength(32767);
        this.nbtBox.setTextColor(FontColor.PURED_GOLD);
        this.nbtBox.setValue(this.nbtHolder.getAsString());
        this.addRenderableWidget(this.nbtBox);

        // Specific Editor
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.stream().toList().get(i);
            String value = this.nbtHolder.get(key).getAsString();
            int width = this.width * 5 / 6 - this.width / 9 - this.textStartX - maxLength;
            EditBox box = this.getEditBox(this.nbtType.get(key), maxLength, i, width);
            box.setEditable(true);
            box.setBordered(true);
            box.setCanLoseFocus(true);
            box.setMaxLength(32767);
            box.setValue(value);
            this.addRenderableWidget(box);
        }
    }

    private EditBox getEditBox(int type, int maxLength, int i, int width) {
        int x = this.textStartX + maxLength;
        int y = this.textStartY - 5 + i * 24;
        int height = 20;
        if (type == CompoundTag.TAG_BYTE) {
            return new BooleanEditBox(x, y, width, height);
        }
        else {
            return new EditBox(this.font, x, y, width, height, new TextComponent(""));
        }
    }
}
