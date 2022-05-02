package com.thana.sugarapi.client.config.handler;

import com.thana.sugarapi.common.utils.config.ConfigEnum;

public enum MouseScroll implements ConfigEnum<MouseScroll> {
    SLOW(1),
    NORMAL(4),
    FAST(8);

    private final int delta;

    MouseScroll(int delta) {
        this.delta = delta;
    }

    @Override
    public MouseScroll parse(String text) {
        for (MouseScroll scroll : values()) {
            if (scroll.name().equalsIgnoreCase(text)) {
                return scroll;
            }
        }
        return null;
    }

    @Override
    public MouseScroll next(MouseScroll scroll) {
        return values()[(scroll.ordinal() + 1) % values().length];
    }

    @Override
    public MouseScroll previous(MouseScroll scroll) {
        return values()[(values().length + (scroll.ordinal() - 1)) % values().length];
    }

    @Override
    public String save(MouseScroll scroll) {
        return scroll.name().toUpperCase();
    }

    public int getDelta() {
        return this.delta;
    }
}
