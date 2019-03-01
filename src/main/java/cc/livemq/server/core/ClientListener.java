package cc.livemq.server.core;

import cc.livemq.core.Connector;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 客户端连接监听线程
 */
@Slf4j
public class ClientListener extends Thread {
    private boolean done = false;
    private final Selector selector;

    /**
     * 连接缓存
     */
    private final Map<String, Connector> connectorMap = new HashMap<>();

    public ClientListener(Selector selector) {
        super("client listener thread");
        this.selector = selector;
    }

    @Override
    public void run() {
        log.info("服务器准备就绪~~");
        do {
            try {
                if(selector.select() == 0) {
                    if(done) {
                        break;
                    }
                    continue;
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    // 检查当前
                    if(key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        Connector connector = new Connector(channel);
                        synchronized (ClientListener.this) {
                            connectorMap.put(connector.getKey(), connector);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!done);
        log.info("服务器已关闭!!");
    }

    public void exit() {
        done = true;
        // 此处唤醒的意义在于 selector 可能处于阻塞操作 select() 中
        selector.wakeup();
    }
}
