package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_7;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.BedAndWeatherPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.steveice10.mc.protocol.data.game.world.notify.ClientNotification;
import com.github.steveice10.mc.protocol.data.game.world.notify.ThunderStrengthValue;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import com.github.steveice10.packetlib.Session;

public class BedAndWeatherTranslator implements BetaToModern<BedAndWeatherPacketData> {

    @Override
    public void translate(BedAndWeatherPacketData packet, BetaClientSession session, Session modernSession) {
        int state = packet.getWeatherState();
        ClientNotification notification;
        switch (state) {
            case 0:
                notification = ClientNotification.INVALID_BED;
                break;
            case 1:
                notification = ClientNotification.START_RAIN;
                break;
            case 2:
                notification = ClientNotification.STOP_RAIN;
                break;
            default:
                notification = ClientNotification.ENTER_CREDITS; //never happens, so...
                break;
        }

        modernSession.send(new ServerNotifyClientPacket(notification, new ThunderStrengthValue(0)));
    }
}
