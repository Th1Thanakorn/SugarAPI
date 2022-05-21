package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.common.utils.FontColor;
import com.thana.sugarapi.common.utils.TextWrapper;
import com.thana.sugarapi.common.utils.coding.OrdinalCodeRepository;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandEditorScreen extends Screen {

    private static final Pattern NON_CHARACTERISTIC = Pattern.compile("\\\\[nrt]");
    private final Minecraft mc = Minecraft.getInstance();

    private List<EditBox> editors = new ArrayList<>();
    private List<OrdinalCodeRepository> repositories;

    public CommandEditorScreen() {
        super(new TextComponent("Command Editor"));
    }

    @Override
    public void init() {
        this.mc.keyboardHandler.setSendRepeatsToGui(true);
        this.editors.clear();
        this.repositories = OrdinalCodeRepository.getRepositories();
        for (int i = 0; i < 10; i++) {
            EditBox editor = new EditBox(this.font, this.width / 3, this.height / 8 + 18 * i, (int) (this.width / 1.5D), 18, TextWrapper.newText());
            editor.setEditable(true);
            editor.setCanLoseFocus(true);
            editor.setBordered(false);
            editor.setMaxLength(32767);

            this.addRenderableWidget(editor);
            this.editors.add(editor);
        }

        for (OrdinalCodeRepository repository : this.repositories) {
            Button button = new Button(20, this.height / 8, 100, 20, TextWrapper.wrapped(repository.getName()), (buttonx) -> {
                int i = 0;
                for (String text : repository.read().split("\n")) {
                    text = text.replaceAll("\\\\[nrt]", "");
                    this.editors.get(i).setValue(text.replaceAll("\n", ""));
                    i++;
                }
            });
            this.addRenderableWidget(button);
        }

        this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 32, 100, 20, TextWrapper.wrapped(""), (button) -> {
            if (this.mc.player != null) {
                for (EditBox box : this.editors) {
                    String value = box.getValue();
                    if (!value.isEmpty()) this.mc.player.chat("/" + value);
                }
            }
        }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        Gui.drawCenteredString(poseStack, this.mc.font, this.title, this.width / 2, 24, FontColor.WHITE);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.mc.keyboardHandler.setSendRepeatsToGui(false);
        super.onClose();
    }
}
