package cc.livemq.core;

import cc.livemq.core.wire.MqttRandomMessage;
import cc.livemq.core.wire.MqttWireMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息接收者
 */
@Slf4j
public final class Receiver implements Closeable {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final SocketChannel channel;
    private final IO io;
    private final MessageEventListener listener;

    public Receiver(SocketChannel channel, IO io, MessageEventListener listener) {
        this.channel = channel;
        this.io = io;
        this.listener = listener;
    }

    /**
     *
     */
    public void receiver() throws IOException {
        if(isClosed.get()){
            throw new IOException("Current channel is closed!");
        }

        io.registerInput(channel, callback);
    }

    @Override
    public void close() throws IOException {
        if(isClosed.compareAndSet(false, true)) {
            // 解除注册
            io.unRegisterInput(channel);
            // 关闭 channel
            channel.close();
        }
    }

    private final InputMessageCallback callback = new InputMessageCallback();

    private class InputMessageCallback implements Runnable {
        @Override
        public void run() {
            input();
        }

        private void input() {
            if(isClosed.get()) {
                log.warn("消息接收者已处于关闭状态，消息接收失败!!");
                return;
            }

            try {
                // 1.创建接收消息的缓存区
                // TODO 此处缓冲区大小注意
                ByteBuffer buffer = ByteBuffer.allocate(4);

                // 2.从 channel 中读取消息到缓冲区
                int len = channel.read(buffer);
                if(len > 0) {
                    MqttRandomMessage message = new MqttRandomMessage("");
                    // 3.消息接收完成回调
                    listener.onMessageArrived(message);
                }else {
                    throw new IOException("Cannot read any data!");
                }
            } catch (Exception e) {
                log.error("消息接收失败!! {}", e);
            }
        }
    }
}
