package com.thana.sugarapi.common.utils;

public class MouseHandler {

    public static boolean isOver(double mouseX, double mouseY, double minX, double minY, double maxX, double maxY) {
        return mouseX >= minX && mouseY >= minY && mouseX <= maxX && mouseY <= maxY;
    }
}
