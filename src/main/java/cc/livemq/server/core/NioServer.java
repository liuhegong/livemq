package cc.livemq.server.core;

import cc.livemq.constant.Constant;
import cc.livemq.core.Context;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 *
 */
@Slf4j
public final class NioServer {
    private Selector selector;
    private ServerSocketChannel server;
    private ClientListener listener;

    public void start() throws IOException {
        Context.start();
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(Constant.SERVER_PORT));
        server.register(selector, SelectionKey.OP_ACCEPT);
        log.info("服务端信息：{}", server.getLocalAddress().toString());

        // 启动客户端连接监听线程
        listener = new ClientListener(selector);
        listener.start();
    }

    public void stop() throws IOException {
        Context.stop();
        if(listener != null) {
            listener.exit();
        }

        server.close();
        selector.close();
    }
}
