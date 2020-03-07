package com.github.dirtpowered.releasetobeta.network.translator.registry;

import com.github.dirtpowered.releasetobeta.network.translator.model.ModernToBeta;
import com.github.steveice10.packetlib.packet.Packet;

import java.util.HashMap;
import java.util.Map;

public class ModernToBetaTranslatorRegistry {
    private final Map<Class<? extends Packet>, ModernToBeta> translators = new HashMap<>();

    public void registerTranslator(final Class<? extends Packet> packet, ModernToBeta translator) {
        translators.put(packet, translator);
    }

    public ModernToBeta getByPacket(final Packet packet) {
        return translators.get(packet.getClass());
    }
}
