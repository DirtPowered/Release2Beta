package com.github.dirtpowered.releasetobeta.network.legacy;

import com.github.steveice10.packetlib.ConnectionListener;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpPacketCodec;
import com.github.steveice10.packetlib.tcp.TcpPacketEncryptor;
import com.github.steveice10.packetlib.tcp.TcpPacketSizer;
import com.github.steveice10.packetlib.tcp.TcpServerSession;
import com.github.steveice10.packetlib.tcp.TcpSession;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

public class _TcpConnectionListener implements ConnectionListener {
    private String host;
    private int port;
    private Server server;

    private EventLoopGroup group;
    private Channel channel;

    _TcpConnectionListener(String host, int port, Server server) {
        this.host = host;
        this.port = port;
        this.server = server;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public boolean isListening() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void bind() {
        this.bind(true);
    }

    @Override
    public void bind(boolean wait) {
        this.bind(wait, null);
    }

    @Override
    public void bind(boolean wait, final Runnable callback) {
        if (this.group != null || this.channel != null) {
            return;
        }

        this.group = new NioEventLoopGroup();
        ChannelFuture future = new ServerBootstrap().channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel channel) {
                InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
                PacketProtocol protocol = server.createPacketProtocol();

                TcpSession session = new TcpServerSession(address.getHostName(), address.getPort(), protocol, server);
                session.getPacketProtocol().newServerSession(server, session);

                channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                channel.config().setOption(ChannelOption.TCP_NODELAY, false);

                ChannelPipeline pipeline = channel.pipeline();

                //TODO: reflection?
                //session.refreshReadTimeoutHandler(channel);
                //session.refreshWriteTimeoutHandler(channel);

                pipeline.addLast("proxied_connection", new ProxiedConnection());
                pipeline.addLast("encryption", new TcpPacketEncryptor(session));
                pipeline.addLast("sizer", new TcpPacketSizer(session));
                pipeline.addLast("codec", new TcpPacketCodec(session));
                pipeline.addLast("manager", session);
            }
        }).group(this.group).localAddress(this.host, this.port).bind();

        if (wait) {
            try {
                future.sync();
            } catch (InterruptedException ignored) {
            }

            channel = future.channel();
            if (callback != null) {
                callback.run();
            }
        } else {
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    channel = future1.channel();
                    if (callback != null) {
                        callback.run();
                    }
                } else {
                    System.err.println("[ERROR] Failed to asynchronously bind connection listener.");
                    if (future1.cause() != null) {
                        future1.cause().printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void close() {
        this.close(false);
    }

    @Override
    public void close(boolean wait) {
        this.close(wait, null);
    }

    @Override
    public void close(boolean wait, final Runnable callback) {
        if (this.channel != null) {
            if (this.channel.isOpen()) {
                ChannelFuture future = this.channel.close();
                if (wait) {
                    try {
                        future.sync();
                    } catch (InterruptedException ignored) {
                    }

                    if (callback != null) {
                        callback.run();
                    }
                } else {
                    future.addListener((ChannelFutureListener) future1 -> {
                        if (future1.isSuccess()) {
                            if (callback != null) {
                                callback.run();
                            }
                        } else {
                            System.err.println("[ERROR] Failed to asynchronously close connection listener.");
                            if (future1.cause() != null) {
                                future1.cause().printStackTrace();
                            }
                        }
                    });
                }
            }

            this.channel = null;
        }

        if (this.group != null) {
            Future<?> future = this.group.shutdownGracefully();
            if (wait) {
                try {
                    future.sync();
                } catch (InterruptedException ignored) {
                }
            } else {
                future.addListener(future12 -> {
                    if (!future12.isSuccess()) {
                        System.err.println("[ERROR] Failed to asynchronously close connection listener.");
                        if (future12.cause() != null) {
                            future12.cause().printStackTrace();
                        }
                    }
                });
            }

            this.group = null;
        }
    }
}
