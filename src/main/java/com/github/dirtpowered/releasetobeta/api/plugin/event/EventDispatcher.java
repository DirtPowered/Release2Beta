package com.github.dirtpowered.releasetobeta.api.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
class EventDispatcher {
    private final Listener listener;
    private final Method method;

    @SneakyThrows
    void fireEvent(Event event) {
        method.invoke(listener, event);
    }
}
