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

    public static MouseScroll fromName(String text) {
        for (MouseScroll scroll : values()) {
            if (scroll.name().equalsIgnoreCase(text)) {
                return scroll;
            }
        }
        return null;
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
    public MouseScroll next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    @Override
    public MouseScroll previous() {
        return values()[(values().length + (this.ordinal() - 1)) % values().length];
    }

    @Override
    public String save() {
        return this.name().toUpperCase();
    }

    public int getDelta() {
        return this.delta;
    }
}
