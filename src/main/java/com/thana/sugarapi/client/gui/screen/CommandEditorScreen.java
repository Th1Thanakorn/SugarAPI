package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.common.utils.FontColor;
import com.thana.sugarapi.common.utils.TextWrapper;
import com.thana.sugarapi.common.utils.coding.OrdinalCodeRepository;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommandEditorScreen extends Screen {

    public static final List<String> commands = new ArrayList<>();

    private static final Pattern NON_CHARACTERISTIC = Pattern.compile("\\\\[nrt]");
    private static final Pattern NON_NUMERICAL_CHARACTER = Pattern.compile("[^a-zA-Z0-9(),#$%*@.!~^+-=_<>? ]");
    private final Minecraft mc = Minecraft.getInstance();

    private final List<EditBox> editors = new ArrayList<>();
    private final List<CommandSuggestions> commandSuggestions = new ArrayList<>();
    private List<OrdinalCodeRepository> repositories;
    private int selectedNode = 0;

    public CommandEditorScreen() {
        super(new TextComponent("Command Editor"));
    }

    @Override
    public void init() {
        this.mc.keyboardHandler.setSendRepeatsToGui(true);
        this.editors.clear();
        this.commandSuggestions.clear();
        this.repositories = OrdinalCodeRepository.getRepositories();
        this.selectedNode = 0;

        for (int i = 0; i < 10; i++) {
            EditBox editor = new EditBox(this.font, (int) (this.width / 4.0D), this.height / 8 + 18 * i, (int) (this.width / 1.5D), 18, TextWrapper.newText());
            editor.setEditable(true);
            editor.setCanLoseFocus(true);
            editor.setBordered(false);
            editor.setMaxLength(32767);
            editor.setResponder((string) -> this.onEdited(editor));

            CommandSuggestions suggestions = new CommandSuggestions(this.mc, this, editor, this.font, false, false, 1, 10, true, -805306368);
            suggestions.updateCommandInfo();

            this.addRenderableWidget(editor);
            this.editors.add(editor);
            this.commandSuggestions.add(suggestions);
        }

        for (OrdinalCodeRepository repository : this.repositories) {
            Button button = new Button((int) (this.width / 4.0D / 6.0D), this.height / 8, (int) (this.width / 6.0D), 20, TextWrapper.wrapped(repository.getName()), (buttonx) -> {
                int i = 0;
                for (String text : repository.read().split("\n")) {
                    text = text.replaceAll(NON_CHARACTERISTIC.pattern(), "");
                    text = text.replaceAll(NON_NUMERICAL_CHARACTER.pattern(), "");
                    this.editors.get(i).setValue("/" + text.replaceAll("\n", ""));
                    i++;
                }
            });
            this.addRenderableWidget(button);
        }

        this.addRenderableWidget(new Button((int) (this.width / 2.0D - 75.0D / 2.0D), this.height - 32, 75, 20, TextWrapper.wrapped("Compile"), (button) -> {
            if (this.mc.player != null) {
                for (EditBox box : this.editors) {
                    String value = box.getValue();
                    if (value.isEmpty()) continue;
                    if (!value.startsWith("/")) {
                        value = "/" + value;
                        box.setValue(value);
                    }
                    this.mc.player.chat(value);
                }
            }
        }));

        for (int i = 0; i < commands.size(); i++) {
            this.editors.get(i).setValue(commands.get(i));
        }
        this.setInitialFocus(this.editors.get(0));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        Gui.drawCenteredString(poseStack, this.mc.font, this.title, this.width / 2, 24, FontColor.WHITE);
        if (this.editors.stream().anyMatch(AbstractWidget::isFocused)) this.selectedNode = -1;
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestions.stream().anyMatch((suggestion) -> suggestion.keyPressed(keyCode, scanCode, modifiers))) {
            return true;
        }
        else if (this.selectedNode != -1 && this.editors.stream().anyMatch((suggestion) -> suggestion.keyPressed(keyCode, scanCode, modifiers)) && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.selectedNode = (this.selectedNode + 1) % this.editors.size();
            this.setFocused(this.editors.get(this.selectedNode));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (int i = 0; i < this.editors.size(); i++) {
            EditBox editBox = this.editors.stream().toList().get(i);
            if (editBox.isFocused()) {
                this.selectedNode = i;
            }
        }
        this.editors.forEach((box) -> box.setFocus(false));
        this.editors.get(this.selectedNode).setFocus(true);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onClose() {
        this.mc.keyboardHandler.setSendRepeatsToGui(false);
        commands.clear();
        this.editors.forEach((box) -> commands.add(box.getValue()));
        super.onClose();
    }

    private void onEdited(EditBox editBox) {
        int j = -1;
        for (int i = 0; i < this.editors.size(); i++) {
            if (this.editors.get(i).equals(editBox)) {
                j = i;
                break;
            }
        }
        if (j == -1) return;
        this.commandSuggestions.get(j).setAllowSuggestions(true);
        this.commandSuggestions.get(j).updateCommandInfo();
    }
}
