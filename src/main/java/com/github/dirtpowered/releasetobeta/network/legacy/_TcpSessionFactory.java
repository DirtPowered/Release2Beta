package com.github.dirtpowered.releasetobeta.network.legacy;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.ConnectionListener;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.SessionFactory;

public class _TcpSessionFactory implements SessionFactory {
    @Override
    public Session createClientSession(Client client) {
        return null;
    }

    @Override
    public ConnectionListener createServerListener(Server server) {
        return new _TcpConnectionListener(server.getHost(), server.getPort(), server);
    }
}
