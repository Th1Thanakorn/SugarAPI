package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.gui.widget.ButtonSelectionList;
import com.thana.sugarapi.common.utils.FontColor;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class MultiKeyBindsScreen extends Screen {

    private final Minecraft mc = Minecraft.getInstance();

    private ButtonSelectionList list;
    public KeyMapping selectedKey;

    public MultiKeyBindsScreen() {
        super(TextWrapper.wrapped("Multiple Keybinds Settings"));
    }

    public void init() {
        this.list = new ButtonSelectionList(this.mc, this);
        this.addWidget(list);
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 150, 20, TextWrapper.wrapped("Remove All"), (button) -> {}));
        this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_DONE, (button) -> this.onClose()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, mouseX, mouseY, partialTicks);
        Gui.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, FontColor.WHITE);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
