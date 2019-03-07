package cc.livemq.client;

import cc.livemq.core.Connector;
import cc.livemq.core.mqtt.wire.MqttAbstractMessage;
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
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final SocketChannel channel;
    private Connector connector;

    public NioClient() throws IOException {
        channel = SocketChannel.open();
    }

    public void connect(String host, int port) throws IOException {
        channel.connect(new InetSocketAddress(host, port));
        // 注意这里设置非阻塞有两种方式:
        // 1.客户端 channel 可以在 connect 成功后设置为非阻塞
        // 2.也可以在 connect 之前设置为非阻塞，connect 成功之后调用 channel.finishConnect() 即可
        channel.configureBlocking(false);
        log.info("已发起服务端连接~~");

        connector = new Connector(channel);
    }
}
