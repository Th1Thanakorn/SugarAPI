package com.thana.sugarapi.common.api.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.List;
import java.util.function.Consumer;

public class EventPoster {

    private static final MultiMap<Class<?>, Consumer<?>> MAP = new MultiMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Event> void post(T event) {
        MinecraftForge.EVENT_BUS.post(event);
        if (MAP.containsKey(event.getClass())) {
            List<Consumer<?>> list = MAP.get(event.getClass());
            for (Consumer<?> consumer : list) {
                Consumer<T> a = (Consumer<T>) consumer;
                a.accept(event);
            }
        }
    }

    public static <T extends Event> void launch(Class<T> event, Consumer<T> bus) {
        MAP.map(event, bus);
    }
}
