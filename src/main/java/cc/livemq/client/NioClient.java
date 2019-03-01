package cc.livemq.client;

import cc.livemq.core.Connector;
import cc.livemq.core.Context;
import cc.livemq.core.wire.MqttWireMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
@Slf4j
public final class NioClient {
    private final AtomicBoolean isClosed = new AtomicBoolean(true);
    private Connector connector;

    public void connect(String host, int port) throws IOException {
        if(isClosed.compareAndSet(true, false)) {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));
            log.info("已发起服务端连接~~");

            connector = new Connector(channel);
        }
    }

    public void disconnect() throws IOException {
        if(isClosed.compareAndSet(false, true)) {
            connector.close();
        }
    }

    public void send(MqttWireMessage message) throws IOException {
        connector.send(message);
    }

    public void test() throws IOException {
        Context.get().getIo().test(connector.getChannel());
    }
}
