package com.github.dirtpowered.releasetobeta.network.translator.registry;

import com.github.dirtpowered.betaprotocollib.model.Packet;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;

import java.util.HashMap;
import java.util.Map;

public class BetaToModernTranslatorRegistry {
    private final Map<Class<? extends Packet>, BetaToModern> translators = new HashMap<>();

    public void registerTranslator(final Class<? extends Packet> packet, BetaToModern translator) {
        translators.put(packet, translator);
    }

    public BetaToModern getByPacket(final Packet packet) {
        return translators.get(packet.getClass());
    }
}
