package com.thana.sugarapi.client.gui.screen;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.gui.widget.button.SliderButton;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.JsonPrimitiveObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SugarSettingsScreen extends Screen {

    public static final HashMap<String, String> ALL_CONFIG = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Minecraft mc = Minecraft.getInstance();
    private final List<Button> modSelectionButtons = new ArrayList<>();
    private final List<Widget> settingsButtons = new ArrayList<>();

    private int buttonSize;
    private int editorX;
    private int editorY;
    private int editorRight;
    private int editorBottom;
    private String shownModId = "";
    private String errorMessage = null;

    public SugarSettingsScreen() {
        super(new TextComponent("SugarAPI Settings"));
    }

    @Override
    public void init() {
        this.buttonSize = Math.min(this.width / 6, 80);
        this.editorX = this.width / 6 + this.buttonSize + 16;
        this.editorY = this.height / 7 - 4;
        this.editorRight = this.width * 5 / 6 + 4;
        this.editorBottom = this.height * 6 / 7 + 4;
        this.shownModId = "";
        this.errorMessage = null;
        this.modSelectionButtons.clear();
        this.settingsButtons.clear();

        this.addRenderableWidget(new Button(this.width / 6 - 30, this.height / 7, 20, 20, new TextComponent("\u27F3"), (button) -> {
            JsonConfig.set(SugarAPI.MOD_ID, "lastConfigId", "");
            this.clearWidgets();
            this.init();
        }));

        int i = 0;
        for (String modid : ALL_CONFIG.keySet()) {
            String displayName = ALL_CONFIG.get(modid);
            Button button;
            this.addRenderableWidget(button = new Button(this.width / 6, this.height / 7 + 16 + 24 * i, this.buttonSize, 20, new TextComponent(displayName), this::showConfig));
            this.modSelectionButtons.add(button);
            i++;
        }

        // Attempt Open Last Page
        String lastOpenedId = JsonConfig.getLastOpenedId();
        if (!lastOpenedId.isEmpty()) {
            this.showConfig(lastOpenedId);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        int colorA = FastColor.ARGB32.color(170, 0, 0, 0);
        int colorB = FastColor.ARGB32.color(170, 0, 0, 0);
        this.fillGradient(poseStack, this.width / 6 - 4, this.height / 7 - 4, this.width * 5 / 6 + 4, this.height * 6 / 7 + 4, -1072689136, -804253680);
        this.fillGradient(poseStack, this.width / 6 + this.buttonSize + 16, this.height / 7 - 4, this.width * 5 / 6 + 4, this.height * 6 / 7 + 4, colorA, colorB);
        Gui.drawString(poseStack, this.font, ChatFormatting.UNDERLINE + "Mods", this.width / 6 + 4, this.height / 7 + 3, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        for (Button button : this.modSelectionButtons) {
            if (button.isHoveredOrFocused() && !button.isFocused()) {
                this.renderComponentTooltip(poseStack, this.getTooltip(button.getMessage().getString()), mouseX, mouseY, this.font);
            }
        }

        // Show Headers
        if (!this.shownModId.isEmpty()) {
            JsonObject object = JsonConfig.modifiableConfig(this.shownModId);
            int i = 0;
            for (String key : object.keySet()) {
                this.font.drawShadow(poseStack, ChatFormatting.AQUA + key, this.editorX + 6, this.editorY + 12 + i * 24, 16777215);
                i++;
            }
        }

        // Show Errors
        if (!StringUtil.isNullOrEmpty(this.errorMessage)) {
            int middleX = (this.editorRight + this.editorX) / 2;
            int middleY = (this.editorBottom + this.editorY) / 2;
            Gui.drawCenteredString(poseStack, this.font, this.errorMessage, middleX, middleY - this.font.lineHeight / 2, 16777215);
        }
    }

    public static void putConfig(String modid, String prettyName) {
        ALL_CONFIG.put(modid, prettyName);
    }

    private List<Component> getTooltip(String name) {
        for (String modid : ALL_CONFIG.keySet()) {
            String modName = ALL_CONFIG.get(modid);
            if (name.equals(modName)) {
                String modVersion = ClientConfig.getModVersion(modid);
                return List.of(
                        new TextComponent(ChatFormatting.GRAY + ChatFormatting.UNDERLINE.toString() + "Mod Info"),
                        new TextComponent(ChatFormatting.WHITE + "Mod Id: " + ChatFormatting.GOLD + modid),
                        new TextComponent(ChatFormatting.WHITE + "Mod Name: " + ChatFormatting.GOLD + modName),
                        new TextComponent(ChatFormatting.WHITE + "Mod Version: " + ChatFormatting.GOLD + modVersion));
            }
        }
        return List.of();
    }

    private void showConfig(Button button) {
        String modName = button.getMessage().getString();
        String modid = null;
        for (String s : ALL_CONFIG.keySet()) {
            if (ALL_CONFIG.get(s).equals(modName)) {
                modid = s;
            }
        }
        this.showConfig(modName, modid);
    }

    private void showConfig(String modid) {
        for (String s : ALL_CONFIG.keySet()) {
            if (s.equals(modid)) {
                String modName = ALL_CONFIG.get(s);
                this.showConfig(modName, modid);
                return;
            }
        }
    }

    private void showConfig(String modName, String modid) {
        this.settingsButtons.clear();
        for (String s : ALL_CONFIG.keySet()) {
            if (ALL_CONFIG.get(s).equals(modName)) {
                modid = s;
            }
        }
        if (modid != null) {
            JsonConfig.set(SugarAPI.MOD_ID, "lastConfigId", modid);
            this.errorMessage = null;
            this.shownModId = modid;
            JsonObject object = JsonConfig.modifiableConfig(this.shownModId);
            int meanLength = 0;
            for (String key : object.keySet()) {
                meanLength = Math.max(meanLength, this.font.width(key));
            }
            meanLength += 8;
            int i = 0;
            for (String key : object.keySet()) {
                JsonElement element = object.get(key);
                int buttonX = this.editorX + 6 + meanLength;
                int buttonY = this.editorY + 6 + i * 24;
                int buttonWidth = this.width * 5 / 6 - 4 - buttonX;
                String finalModid = modid;

                // Primitive Value
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();

                    // Boolean
                    if (primitive.isBoolean()) {
                        Button button = new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent(primitive.getAsBoolean() ? ChatFormatting.GREEN + "YES" : ChatFormatting.RED + "NO"), (b) -> {
                            boolean value = JsonConfig.modifiableConfig(finalModid).get(key).getAsBoolean();
                            JsonConfig.set(finalModid, key, !value);
                            b.setMessage(new TextComponent(!value ? ChatFormatting.GREEN + "YES" : ChatFormatting.RED + "NO"));
                        });
                        this.addRenderableWidget(button);
                        this.settingsButtons.add(button);
                    }

                    // String
                    else if (primitive.isString()) {
                        EditBox box = new EditBox(this.font, buttonX + 1, buttonY, buttonWidth - 2, 18, TextComponent.EMPTY);
                        box.setEditable(true);
                        box.setCanLoseFocus(true);
                        box.setMaxLength(32767);
                        box.setValue(JsonConfig.modifiableConfig(modid).get(key).getAsString());
                        box.setResponder((text) -> JsonConfig.set(finalModid, key, text));
                        this.addRenderableWidget(box);
                        this.settingsButtons.add(box);
                    }

                    // Number
                    // TODO
                    else if (primitive.isNumber()) {
                        Number number = primitive.getAsNumber();

                        // Integer Value
                        if (number instanceof Integer value) {
                            this.addRenderableWidget(new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent("Simple Button"), (b) -> {}));
                        }
                        else {
                            this.addRenderableWidget(new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent("Simple Button"), (b) -> {}));
                        }
                    }
                }

                // Json Object
                else if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.has("isPrimitive")) {
                        JsonPrimitiveObject primitiveObject = GSON.fromJson(jsonObject, JsonPrimitiveObject.class);

                        // Integer
                        // TODO
                        if (primitiveObject.getType().equals("int")) {
                            int min = primitiveObject.getMin().intValue();
                            int max = primitiveObject.getMax().intValue();
                            SliderButton slider = new SliderButton(buttonX, buttonY, buttonWidth, 20, new TextComponent("Value: ").withStyle(ChatFormatting.GOLD), TextComponent.EMPTY, min, max, primitiveObject.getValue().intValue(), true, (widget) -> {
                                int value = widget.getValueInt();
                                JsonConfig.set(finalModid, key, value, min, max);
                            });
                            this.addRenderableWidget(slider);
                            this.settingsButtons.add(slider);
                        }

                        // Double
                        if (primitiveObject.getType().equals("double")) {
                            double min = primitiveObject.getMin().doubleValue();
                            double max = primitiveObject.getMax().doubleValue();
                            SliderButton slider = new SliderButton(buttonX, buttonY, buttonWidth, 20, new TextComponent("Value: ").withStyle(ChatFormatting.GOLD), TextComponent.EMPTY, min, max, primitiveObject.getValue().doubleValue(), 0.1D, 0, true, (widget) -> {
                                double value = widget.getValue();
                                if (this.mc.player != null) {
                                    this.mc.player.getAbilities().setFlyingSpeed(0.05F * (float) value);
                                }
                                JsonConfig.set(finalModid, key, value, min, max);
                            });
                            this.addRenderableWidget(slider);
                            this.settingsButtons.add(slider);
                        }
                    }
                    else {
                        this.addRenderableWidget(new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent("Simple Button"), (b) -> {}));
                    }
                }

                // Json Array
                // TODO
                else if (element.isJsonArray()) {
                    this.addRenderableWidget(new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent("Simple Button"), (b) -> {}));
                }

                // Json Null
                // TODO
                else if (element.isJsonNull()) {
                    this.addRenderableWidget(new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent("Simple Button"), (b) -> {}));
                }
                i++;
            }
        }
        else {
            this.errorMessage = ChatFormatting.RED + "Can't create config for mod: " + modName;
            this.shownModId = "";
        }
    }
}
