package com.thana.sugarapi.client.gui.widget.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.common.api.gui.ISuggestBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class BooleanEditBox extends EditBox implements ISuggestBox {

    private static final List<String> BOX_SUGGESTIONS = List.of("true", "false");

    private boolean hasError = false;

    public BooleanEditBox(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, new TextComponent(""));
        this.setEditable(true);
        this.setMaxLength(32767);
        this.setCanLoseFocus(true);
        this.setResponder((text) -> {
            this.setSuggestion(this.getSuggestion());
            this.updateText(text);
        });
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int x, int y, float partialTicks) {
        super.renderButton(poseStack, x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        String suggestion = this.replaceText();
        if (keyCode == GLFW.GLFW_KEY_TAB && this.getSuggestion() != null && suggestion != null && this.isFocused()) {
            this.setValue(suggestion);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setValue(String text) {
        switch (text) {
            case "0b", "false" -> {
                super.setValue("false");
                this.setTextColor(5636095);
                this.setHasError(false);
            }
            case "1b", "true" -> {
                super.setValue("true");
                this.setTextColor(5636095);
                this.setHasError(false);
            }
            default -> {
                super.setValue(text);
                this.setTextColor(16733525);
                this.setHasError(true);
            }
        }
    }

    public void updateText(String text) {
        switch (text) {
            case "0b", "false", "1b", "true" -> {
                this.setTextColor(5636095);
                this.setHasError(false);
            }
            default -> {
                this.setTextColor(16733525);
                this.setHasError(true);
            }
        }
    }

    private void setHasError(boolean error) {
        this.hasError = error;
    }

    public boolean hasError() {
        return this.hasError;
    }

    @Override
    public String getSuggestion() {
        String replaceText = this.replaceText();
        if (replaceText == null) {
            return null;
        }
        else {
            return replaceText.replaceAll(this.getValue(), "");
        }
    }

    private String replaceText() {
        String value = this.getValue();
        for (String suggestion : BOX_SUGGESTIONS) {
            if (suggestion.toLowerCase().startsWith(value.toLowerCase()) && !suggestion.equals(value) && !value.isEmpty()) {
                return suggestion;
            }
        }
        return null;
    }
}
