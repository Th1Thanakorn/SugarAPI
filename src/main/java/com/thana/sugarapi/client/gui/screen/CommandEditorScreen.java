package com.thana.sugarapi.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thana.sugarapi.common.utils.coding.OrdinalCodeRepository;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class CommandEditorScreen extends Screen {

    private List<OrdinalCodeRepository> repositories;

    public CommandEditorScreen() {
        super(new TextComponent("Command Editor"));
    }

    @Override
    public void init() {
        this.repositories = OrdinalCodeRepository.getRepositories();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
