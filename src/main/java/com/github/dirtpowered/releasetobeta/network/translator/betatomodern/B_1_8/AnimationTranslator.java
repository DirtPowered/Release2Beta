package com.github.dirtpowered.releasetobeta.network.translator.betatomodern.B_1_8;

import com.github.dirtpowered.betaprotocollib.packet.Version_B1_7.data.AnimationPacketData;
import com.github.dirtpowered.releasetobeta.network.session.BetaClientSession;
import com.github.dirtpowered.releasetobeta.network.translator.model.BetaToModern;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.github.steveice10.mc.protocol.data.game.entity.player.Animation;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import com.github.steveice10.packetlib.Session;

public class AnimationTranslator implements BetaToModern<AnimationPacketData> {

    @Override
    public void translate(AnimationPacketData packet, BetaClientSession session, Session modernSession) {
        Utils.debug(packet);
        int entityId = packet.getEntityId();
        int animation = packet.getAnimate();
        Animation modernAnimation;

        switch (animation) {
            case 0:
                modernAnimation = Animation.SWING_ARM;
                break;
            case 1:
                modernAnimation = Animation.DAMAGE;
                break;
            case 2:
                modernAnimation = Animation.LEAVE_BED;
                break;
            case 5:
                modernAnimation = Animation.EAT_FOOD;
                break;
            default:
                modernAnimation = Animation.SWING_ARM;
                break;
        }

        modernSession.send(new ServerEntityAnimationPacket(entityId, modernAnimation));
    }
}
