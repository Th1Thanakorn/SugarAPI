package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.gui.widget.ButtonSelectionList;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.utils.FontColor;
import com.thana.sugarapi.common.utils.JsonConfig;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.HashMap;

public class MultiKeyBindsScreen extends Screen {

    private final Minecraft mc = Minecraft.getInstance();

    public HashMap<KeyMapping, int[]> keys = reloadKeys();
    private ButtonSelectionList list;
    public Button selectedButton;
    public KeyMapping selectedAddKey;
    public KeyMapping selectedRemoveKey;

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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedAddKey != null) {
            if (this.selectedAddKey.getKey().getValue() != keyCode) {
                JsonConfig.setList(SugarAPI.MOD_ID, "multiKeys", toList(this.selectedAddKey.getName(), keyCode));
            }
            this.selectedAddKey = null;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public HashMap<String, String> toList(String key, int keyCode) {
        HashMap<String, String> map = new HashMap<>();
        for (KeyMapping mapping : this.keys.keySet()) {
            if (key.equals(mapping.getName())) {
                map.put(mapping.getName(), Arrays.toString(ArrayUtils.add(this.keys.get(mapping), keyCode)).replaceAll("\\[", "").replaceAll("]", "").trim());
            }
            map.put(mapping.getName(), Arrays.toString(this.keys.get(mapping)).replaceAll("\\[", "").replaceAll("]", "").trim());
        }
        return map;
    }

    public HashMap<KeyMapping, int[]> reloadKeys() {
        return Util.make(new HashMap<>(), (map) -> {
            HashMap<String, String> multiKeys = ClientConfig.readList("multiKeys");
            for (String key : multiKeys.keySet()) {
                for (KeyMapping mapping : this.minecraft.options.keyMappings) {
                    if ((mapping.getName()).equals(key)) {
                        String[] numKeys = multiKeys.get(key).split(",");
                        int[] keySet = new int[numKeys.length];
                        for (int i = 0; i < numKeys.length; i++) {
                            keySet[i] = Integer.parseInt(numKeys[i]);
                        }
                        map.put(mapping, keySet);
                        break;
                    }
                }
            }
        });
    }
}
