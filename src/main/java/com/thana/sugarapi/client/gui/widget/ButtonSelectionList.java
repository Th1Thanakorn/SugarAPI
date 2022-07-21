package com.thana.sugarapi.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.client.config.ClientConfig;
import com.thana.sugarapi.client.gui.screen.MultiKeyBindsScreen;
import com.thana.sugarapi.common.utils.TextWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ButtonSelectionList extends ContainerObjectSelectionList<ButtonSelectionList.Entry> {

    private final HashMap<KeyMapping, int[]> keys = Util.make(new HashMap<>(), (map) -> {
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
    private final MultiKeyBindsScreen screen;
    private int maxNameWidth;

    public ButtonSelectionList(Minecraft minecraft, MultiKeyBindsScreen screen) {
        super(minecraft, screen.width + 45, screen.height, 20, screen.height - 32, 20);
        this.screen = screen;
        KeyMapping[] mappings = ArrayUtils.clone(this.screen.getMinecraft().options.keyMappings);
        Arrays.sort(mappings);
        String s = null;

        for(KeyMapping key : mappings) {
            String s1 = key.getCategory();
            if (!s1.equals(s)) {
                s = s1;
                this.addEntry(new ButtonSelectionList.CategoryEntry(new TranslatableComponent(s1)));
            }

            Component component = new TranslatableComponent(key.getName());
            int i = this.screen.getMinecraft().font.width(component);
            if (i > this.maxNameWidth) {
                this.maxNameWidth = i;
            }

            this.addEntry(new ButtonSelectionList.KeyEntry(key, component));
        }
    }

    @Override
    public int getScrollbarPosition() {
        return super.getScrollbarPosition() + 35;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<ButtonSelectionList.Entry> {}

    @OnlyIn(Dist.CLIENT)
    public class CategoryEntry extends Entry {

        private final Component name;
        private final int width;

        public CategoryEntry(Component p_193886_) {
            this.name = p_193886_;
            this.width = ButtonSelectionList.this.minecraft.font.width(this.name);
        }

        public void render(PoseStack poseStack, int len, int qy, int qx, int p_193892_, int p_193893_, int mouseX, int mouseY, boolean p_193896_, float partialTicks) {
            ButtonSelectionList.this.minecraft.font.draw(poseStack, this.name, (float)(ButtonSelectionList.this.minecraft.screen.width / 2 - this.width / 2), (float)(qy + p_193893_ - 9 - 1), 16777215);
        }

        public boolean changeFocus(boolean p_193900_) {
            return false;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                public void updateNarration(NarrationElementOutput narration) {
                    narration.add(NarratedElementType.TITLE, ButtonSelectionList.CategoryEntry.this.name);
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends Entry {

        private final KeyMapping keyMapping;
        private final Component name;
        private final Button showButton;
        private final Button addButton;

        public KeyEntry(KeyMapping keyMapping, Component name) {
            this.keyMapping = keyMapping;
            this.name = name;
            this.showButton = new Button(0, 0, 75, 20, name, (button) -> ButtonSelectionList.this.screen.selectedKey = keyMapping) {

                @NotNull
                public MutableComponent createNarrationMessage() {
                    return keyMapping.isUnbound() ? new TranslatableComponent("narrator.controls.unbound", name) : new TranslatableComponent("narrator.controls.bound", name, super.createNarrationMessage());
                }
            };

            this.addButton = new Button(75 + 6, 0, 20, 20, TextWrapper.wrapped("+"), (button) -> {});
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.showButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.showButton);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.showButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
            return this.showButton.mouseReleased(mouseX, mouseY, mouseButton);
        }

        @Override
        public void render(PoseStack poseStack, int len, int qy, int qx, int st, int ug, int mouseX, int mouseY, boolean p_93531_, float partialTicks) {
            boolean flag = ButtonSelectionList.this.screen.selectedKey == this.keyMapping;
            ArrayList<Integer> keys = new ArrayList<>();
            if (ButtonSelectionList.this.keys.containsKey(this.keyMapping)) {
                for (int i : ButtonSelectionList.this.keys.get(this.keyMapping)) {
                    keys.add(i);
                }
            }
            float f = (float)(qx + 90 - ButtonSelectionList.this.maxNameWidth);
            ButtonSelectionList.this.minecraft.font.draw(poseStack, this.name, f, (float)(qy + ug / 2 - 9 / 2), 16777215);
            this.showButton.x = qx + 105;
            this.showButton.y = qy;
            this.showButton.setMessage(keys.isEmpty() ? TextWrapper.wrapped(ChatFormatting.RED + "NONE") : this.formatKeys(keys));
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true;
            if (!this.keyMapping.isUnbound()) {
                for (KeyMapping keymapping : ButtonSelectionList.this.minecraft.options.keyMappings) {
                    if (keymapping != this.keyMapping && this.keyMapping.same(keymapping)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keymapping.hasKeyModifierConflict(this.keyMapping);
                    }
                }
            }
            if (flag) {
                this.showButton.setMessage((new TextComponent("> ")).append(this.showButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
            else if (flag1) {
                this.showButton.setMessage(this.showButton.getMessage().copy().withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
            }
            this.showButton.render(poseStack, mouseX, mouseY, partialTicks);
        }

        private Component formatKeys(ArrayList<Integer> keys) {
            StringBuilder builder = new StringBuilder();
            for (int key : keys) {
                int scanCode = GLFW.glfwGetKeyScancode(key);
                String keyName = GLFW.glfwGetKeyName(key, scanCode);
                builder.append(keyName).append(" ");
            }
            String g = builder.toString().trim().replaceAll(" ", ",");
            return TextWrapper.wrapped(g);
        }
    }
}
