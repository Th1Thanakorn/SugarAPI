package com.thana.sugarapi.common.macro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thana.sugarapi.client.keys.ModKeys;
import com.thana.sugarapi.common.api.annotations.MacroHolder;
import com.thana.sugarapi.common.api.annotations.MacroInjection;
import com.thana.sugarapi.common.api.annotations.MacroListener;
import com.thana.sugarapi.common.api.oid.OIDClass;
import com.thana.sugarapi.common.api.oid.Synchronized;
import com.thana.sugarapi.common.core.SugarAPI;
import com.thana.sugarapi.common.macro.MacroObject.Task;
import com.thana.sugarapi.common.macro.type.KeyPressMacro;
import com.thana.sugarapi.common.macro.type.MessagingMacro;
import com.thana.sugarapi.common.macro.type.MouseClickMacro;
import com.thana.sugarapi.common.utils.SimpleLogger;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileReader;

@SuppressWarnings("all")
@OIDClass
@MacroHolder(SugarAPI.MOD_ID)
public class Macro implements MacroInjection {

    private static final String MACRO_DIR = "./macro/";
    private static final String MACRO_FILE = MACRO_DIR + SugarAPI.MOD_ID + "-macro.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final SimpleLogger LOGGER = new SimpleLogger("Macro");

    private final Minecraft mc = Minecraft.getInstance();
    private double mouseX = this.mc.mouseHandler.xpos();
    private double mouseY = this.mc.mouseHandler.ypos();
    private MacroObject macroObject = null;

    @Synchronized
    public void registerMacro() throws Exception {
        MacroEngine.importNew(this);
    }

    @Override
    @SubscribeEvent
    public void activateMacro(TickEvent.ClientTickEvent event) {
        if (MacroEngine.interrupted(this)) return;
        if (this.macroObject != null) {
            MacroEngine.onExecuted(this);
        }
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) throws Exception {
        if (ModKeys.KEY_REFRESH_MACRO.isDown()) {
            MacroEngine.onLoad(this);
            if (this.mc.player != null) {
                this.mc.player.sendMessage(new TextComponent("Macro Refreshed!"), Util.NIL_UUID);
            }
        }
    }

    @SubscribeEvent
    public void drawScreenEvent(ScreenEvent.DrawScreenEvent event) {
        this.mouseX = event.getMouseX();
        this.mouseY = event.getMouseY();
    }

    @Override
    public void createMacro() throws Exception {
        File dir = new File(MACRO_DIR);
        File macro = new File(MACRO_FILE);
        dir.mkdirs();
        macro.createNewFile();
        MacroEngine.onLoad(this);
    }

    @Override
    @MacroListener
    public void onLoad() throws Exception {
        File macro = new File(MACRO_FILE);
        FileReader reader = new FileReader(macro);
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            builder.append((char) c);
        }
        MacroObject macroObject = GSON.fromJson(builder.toString(), MacroObject.class);
        if (macroObject == null) {
            LOGGER.warning("Macro could not be loadded, skipped 1 task");
            return;
        }
        if (!macroObject.isValid()) {
            MacroEngine.interrupt(this);
        }
        this.macroObject = macroObject;
    }

    @Override
    @MacroListener
    public void onExecuted() {
        Task[] tasks = this.macroObject.getTasks();
        for (Task task : tasks) {
            String optional = task.getOptional();
            int delay = task.getDelay();
            switch (task.getType()) {
                case "press_key" -> KeyPressMacro.press(optional, delay);
                case "mouse_click" -> MouseClickMacro.click(optional, delay, this.mouseX, this.mouseY);
                case "send_message" -> MessagingMacro.sendChat(optional, delay);
                default -> LOGGER.warning("Unknown macro type: " + task.getType());
            }
        }
    }
}