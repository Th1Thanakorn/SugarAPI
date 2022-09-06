package com.thana.sugarapi.client.gui.screen;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.config.handler.MouseScroll;
import com.thana.sugarapi.client.event.*;
import com.thana.sugarapi.client.gui.widget.button.ScrollingTabButton;
import com.thana.sugarapi.client.gui.widget.button.SliderButton;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.utils.*;
import com.thana.sugarapi.common.utils.config.EnumSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SugarSettingsScreen extends Screen {

    public static final HashMap<String, String> ALL_CONFIG = new HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Minecraft mc = Minecraft.getInstance();
    private final Window window = this.mc.getWindow();
    private final List<Button> modSelectionButtons = new ArrayList<>();
    private final List<AbstractWidget> settingsButtons = new ArrayList<>();
    private final HashMap<String, AbstractWidget> keySettingsMap = new HashMap<>();

    private int buttonSize;
    private int editorX;
    private int editorY;
    private int editorRight;
    private int editorBottom;
    private int editorWidth;
    private int editorHeight;
    private int buttonTookSize;
    private double scrollDelta;
    private long lastToastShown;
    private String shownModId = "";
    private String errorMessage = null;
    private String toastText = "";
    private Button refreshButton = null;
    private ScrollingTabButton sliderSettings = null;

    public SugarSettingsScreen() {
        super(new TextComponent("SugarAPI Settings"));
    }

    @Override
    public void init() {
        this.mc.keyboardHandler.setSendRepeatsToGui(true);
        this.buttonSize = Math.min(this.width / 6, 80);
        this.editorX = this.width / 6 + this.buttonSize + 16;
        this.editorY = this.height / 7 - 4;
        this.editorRight = this.width * 5 / 6 + 4;
        this.editorBottom = this.height * 6 / 7 + 4;
        this.editorWidth = this.editorRight - this.editorX;
        this.editorHeight = this.editorBottom - this.editorY;
        this.shownModId = "";
        this.toastText = "";
        this.errorMessage = null;
        this.buttonTookSize = 0;
        this.scrollDelta = 0.0D;
        this.lastToastShown = -1L;
        this.refreshButton = null;
        this.sliderSettings = null;
        this.modSelectionButtons.clear();
        this.settingsButtons.clear();
        this.keySettingsMap.clear();

        this.addRenderableWidget(this.refreshButton = new Button(this.width / 6 - 30, this.height / 7, 20, 20, new TextComponent("\u27F3"), (button) -> this.refreshPage()));
        this.addRenderableWidget(new ScrollingTabButton(this.width / 6 + this.buttonSize + 12, this.height / 7 - 4, 4, this.height * 5 / 7 + 8, (slide) -> {
        }));
        this.addRenderableWidget(this.sliderSettings = new ScrollingTabButton(this.width * 5 / 6, this.height / 7 - 4, 4, this.height * 5 / 7 + 8, (slide) -> {
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
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        int colorA = FastColor.ARGB32.color(170, 0, 0, 0);
        int colorB = FastColor.ARGB32.color(170, 0, 0, 0);
        this.fillGradient(poseStack, this.width / 6 - 4, this.height / 7 - 4, this.width * 5 / 6 + 4, this.height * 6 / 7 + 4, -1072689136, -804253680);
        this.fillGradient(poseStack, this.width / 6 + this.buttonSize + 16, this.height / 7 - 4, this.width * 5 / 6 + 4, this.height * 6 / 7 + 4, colorA, colorB);
        Gui.drawString(poseStack, this.font, ChatFormatting.UNDERLINE + "Mods", this.width / 6 + 4, this.height / 7 + 3, 16777215);

        // Render Decorations
        List<Widget> complementWidgets = this.renderables.stream().filter((obj) -> obj instanceof AbstractWidget && !this.settingsButtons.contains(obj)).toList();
        complementWidgets.forEach((w) -> w.render(poseStack, mouseX, mouseY, partialTicks));

        // Sliding Pane
        if (this.sliderSettings != null) {
            int showHeight = this.height * 5 / 7 + 8;
            this.sliderSettings.drawHeight = (float) showHeight / (float) this.buttonTookSize * (float) showHeight;
            this.sliderSettings.y = (int) (this.height / 7 - 4 + this.scrollDelta);
        }

        // Render Settings
        poseStack.pushPose();
        if (!this.settingsButtons.isEmpty()) {
            double scale = this.mc.getWindow().getGuiScale();
            int startX = this.width / 6 - 30;
            int startY = this.height / 7;
            int width = this.width * 5 / 6;
            int height = this.height * 5 / 7 + 4;
            RenderSystem.enableScissor((int) (startX * scale), (int) (startY * scale), (int) (width * scale), (int) (height * scale));
            int i = 0;
            for (AbstractWidget widget : this.settingsButtons) {
                widget.y = (int) (this.editorY + 6 + i * 24 - this.scrollDelta);
                i++;
            }
        }
        if (!this.shownModId.isEmpty()) {
            JsonObject object = JsonConfig.modifiableConfig(this.shownModId);
            int i = 0;
            for (String key : object.keySet()) {
                ConfigNameCreationEvent event = new ConfigNameCreationEvent(this.shownModId, key);
                MinecraftForge.EVENT_BUS.post(event);
                if (!event.shouldRender()) {
                    continue;
                }
                String obfKey = event.isCanceled() ? event.getReturn() : key;
                this.font.drawShadow(poseStack, this.getHeaderFormatting() + obfKey, (float) (this.editorX + 6), (float) (this.editorY + 12 + i * 24 - this.scrollDelta), 16777215);
                i++;
            }
        }
        this.settingsButtons.forEach((w) -> w.render(poseStack, mouseX, mouseY, partialTicks));
        RenderSystem.disableScissor();
        poseStack.popPose();

        // Render Tooltip
        for (Button button : this.modSelectionButtons) {
            if (button.isHoveredOrFocused() && !button.isFocused()) {
                this.renderComponentTooltip(poseStack, this.getTooltip(button.getMessage().getString()), mouseX, mouseY, this.font);
            }
        }
        for (String key : this.keySettingsMap.keySet()) {
            AbstractWidget widget = this.keySettingsMap.get(key);
            if (widget.isHoveredOrFocused() && !widget.isFocused()) {
                ConfigTooltipEvent event = new ConfigTooltipEvent(this.shownModId, key);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    this.renderComponentTooltip(poseStack, event.getTooltip(), mouseX, mouseY, this.font);
                }
            }
        }
        if (this.refreshButton.isHoveredOrFocused() && !this.refreshButton.isFocused()) {
            this.renderComponentTooltip(poseStack, List.of(TextWrapper.wrapped("Refresh " + ChatFormatting.GOLD + "(Ctrl+R)")), mouseX, mouseY, this.font);
        }

        // Show Errors
        if (!StringUtil.isNullOrEmpty(this.errorMessage)) {
            int middleX = (this.editorRight + this.editorX) / 2;
            int middleY = (this.editorBottom + this.editorY) / 2;
            Gui.drawCenteredString(poseStack, this.font, this.errorMessage, middleX, middleY - this.font.lineHeight / 2, FontColor.WHITE);
        }

        // Render Toast
        if (!this.toastText.isEmpty()) {
            long now = System.currentTimeMillis();
            int timeElapsed = (int) (now - this.lastToastShown);
            int timeRemaining = Mth.clamp(6000 - timeElapsed, 0, 6000);
            if (timeElapsed > 6000) {
                this.toastText = "";
                this.lastToastShown = -1L;
            }
            int alphaInt = 255;
            if (timeElapsed <= 2000) {
                float alpha = Mth.clamp(timeElapsed / 10.0F / 5.0F * 0.5F, 0.0F, 1.0F);
                alphaInt = (int) (alpha * 255);
            }
            if (timeRemaining <= 2000) {
                float alpha = Mth.clamp(timeRemaining / 10.0F / 5.0F * 0.5F, 0.0F, 1.0F);
                alphaInt = (int) (alpha * 255);
            }
            int textColor = ARGBHelper.toChatColor(alphaInt, 255, 255, 255);
            Gui.drawCenteredString(poseStack, this.font, this.toastText, this.width / 2, this.editorY - 18, textColor);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int deltaMultiplier = EnumSettings.parse(MouseScroll.NORMAL, SugarAPI.MOD_ID, "scrollDelta").getDelta();
        if ((this.buttonTookSize - this.scrollDelta > this.editorHeight && delta < 0.0D) || (delta > 0.0D && this.scrollDelta > 0)) {
            this.scrollDelta -= deltaMultiplier * delta;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseY < this.editorY || mouseY > this.editorBottom) {
            return false;
        }
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractWidget widget && listener.isMouseOver(mouseX, mouseY) && mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT && this.keySettingsMap.containsValue(widget)) {
                this.setFocused(listener);
                for (String key : this.keySettingsMap.keySet()) {
                    AbstractWidget value = this.keySettingsMap.get(key);
                    if (value.equals(widget)) {
                        MinecraftForge.EVENT_BUS.post(new RightClickConfigButtonEvent(this.shownModId, key, widget, this.mc.getSoundManager()));
                        break;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (InputConstants.isKeyDown(this.window.getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) && keyCode == GLFW.GLFW_KEY_R) {
            this.refreshPage();
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    public void onClose() {
        this.mc.keyboardHandler.setSendRepeatsToGui(false);
        super.onClose();
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
        return List.of(new TextComponent(ChatFormatting.RED + "Could not find modid related to!"));
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
        this.setToastText("Currently showing for: " + ChatFormatting.RED + modName);
        this.settingsButtons.forEach(this::removeWidget);
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
            int textLength = 0;
            for (String key : object.keySet()) {
                ConfigNameCreationEvent event = new ConfigNameCreationEvent(modid, key);
                MinecraftForge.EVENT_BUS.post(event);
                String obfKey = event.isCanceled() ? event.getReturn() : key;
                textLength = Math.max(textLength, this.font.width(obfKey));
            }
            textLength += 8;
            int i = 0;
            this.buttonTookSize = this.editorY + 6 + object.size() * 24;
            for (String key : object.keySet()) {
                JsonElement element = object.get(key);
                int buttonX = this.editorX + 6 + textLength;
                int buttonY = this.editorY + 6 + i * 24;
                int buttonWidth = this.width * 5 / 6 - 10 - buttonX;
                String finalModid = modid;

                // Event Creation
                ConfigCreationEvent creationEvent = new ConfigCreationEvent(modid, key, element, buttonX, buttonY, buttonWidth, 20);
                SpecialJobEvent jobEvent = new SpecialJobEvent(modid, key);
                MinecraftForge.EVENT_BUS.post(creationEvent);
                MinecraftForge.EVENT_BUS.post(jobEvent);
                Runnable customJob = jobEvent.getJob();
                if (creationEvent.isStopped()) continue;
                if (creationEvent.isCanceled()) {
                    AbstractWidget setting = creationEvent.getSetting();
                    if (setting != null) {
                        this.addRenderableWidget(setting);
                        this.settingsButtons.add(setting);
                        this.keySettingsMap.put(key, setting);
                        i++;
                        continue;
                    }
                }

                // Primitive Value
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();

                    // Boolean
                    if (primitive.isBoolean()) {
                        Button button = new Button(buttonX, buttonY, buttonWidth, 20, new TextComponent(primitive.getAsBoolean() ? ChatFormatting.GREEN + "YES" : ChatFormatting.RED + "NO"), (b) -> {
                            boolean value = JsonConfig.modifiableConfig(finalModid).get(key).getAsBoolean();
                            JsonConfig.set(finalModid, key, !value);
                            b.setMessage(new TextComponent(!value ? ChatFormatting.GREEN + "YES" : ChatFormatting.RED + "NO"));
                            MinecraftForge.EVENT_BUS.post(new ConfigChangeEvent(finalModid, key));
                            customJob.run();
                        });
                        this.addRenderableWidget(button);
                        this.settingsButtons.add(button);
                        this.keySettingsMap.put(key, button);
                    }

                    // String
                    else if (primitive.isString()) {
                        EditBox box = new EditBox(this.font, buttonX + 1, buttonY, buttonWidth - 2, 18, TextComponent.EMPTY);
                        box.setEditable(true);
                        box.setCanLoseFocus(true);
                        box.setMaxLength(32767);
                        box.setValue(JsonConfig.modifiableConfig(modid).get(key).getAsString());
                        box.setResponder((text) -> {
                            JsonConfig.set(finalModid, key, text);
                            MinecraftForge.EVENT_BUS.post(new ConfigChangeEvent(finalModid, key));
                            customJob.run();
                        });
                        this.addRenderableWidget(box);
                        this.settingsButtons.add(box);
                        this.keySettingsMap.put(key, box);
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
                                MinecraftForge.EVENT_BUS.post(new ConfigChangeEvent(finalModid, key));
                                customJob.run();
                            });
                            this.addRenderableWidget(slider);
                            this.settingsButtons.add(slider);
                            this.keySettingsMap.put(key, slider);
                        }

                        // Double
                        if (primitiveObject.getType().equals("double")) {
                            double min = primitiveObject.getMin().doubleValue();
                            double max = primitiveObject.getMax().doubleValue();
                            SliderButton slider = new SliderButton(buttonX, buttonY, buttonWidth, 20, new TextComponent("Value: ").withStyle(ChatFormatting.GOLD), TextComponent.EMPTY, min, max, primitiveObject.getValue().doubleValue(), 0.1D, 0, true, (widget) -> {
                                double value = widget.getValue();
                                JsonConfig.set(finalModid, key, value, min, max);
                                MinecraftForge.EVENT_BUS.post(new ConfigChangeEvent(finalModid, key));
                                customJob.run();
                            });
                            this.addRenderableWidget(slider);
                            this.settingsButtons.add(slider);
                            this.keySettingsMap.put(key, slider);
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

    private ChatFormatting getHeaderFormatting() {
        String colorString = ClientConfig.getString("headerColor");
        ChatFormatting formatting = ChatFormatting.getByName(colorString);
        return formatting != null ? formatting : ChatFormatting.AQUA;
    }

    private void refreshPage() {
        JsonConfig.set(SugarAPI.MOD_ID, "lastConfigId", "");
        this.clearWidgets();
        this.init();
        this.setToastText("Reloaded!");
    }

    private void setToastText(String toastText) {
        this.toastText = toastText;
        this.lastToastShown = System.currentTimeMillis();
    }
}
