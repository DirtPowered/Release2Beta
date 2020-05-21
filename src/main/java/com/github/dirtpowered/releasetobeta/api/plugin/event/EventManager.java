package com.github.dirtpowered.releasetobeta.api.plugin.event;

import com.github.dirtpowered.releasetobeta.api.plugin.annotations.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<Class<? extends Event>, List<EventDispatcher>> events = new HashMap<>();

    public <T extends Event> void fireEvent(T event) {
        events.get(event.getClass()).forEach(handler -> handler.fireEvent(event));
    }

    public void registerListener(Listener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods()).forEach(method -> {
            if (!method.isAnnotationPresent(Handler.class)) //make @Handler required
                return;

            Class<?>[] types = method.getParameterTypes();
            if (types.length == 0)
                return;

            Class<?> aClass = types[0];
            if (Event.class.isAssignableFrom(types[0])) {
                @SuppressWarnings("unchecked")
                Class<? extends Event> event = (Class<? extends Event>) aClass;
                if (!events.containsKey(event)) events.put(event, new ArrayList<>());

                List<EventDispatcher> eventDispatchers = this.events.get(event);
                eventDispatchers.add(new EventDispatcher(listener, method));
            }
        });
    }

    public void clear() {
        events.clear();
    }
}
