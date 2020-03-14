package com.github.dirtpowered.releasetobeta.data.entity.model;

import com.github.steveice10.packetlib.Session;

public interface Mob {

    void onDeath(Session session);

    void onDamage(Session session);
}
