package cc.livemq.core;

import cc.livemq.core.wire.MqttWireMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息发送者
 */
@Slf4j
public final class Sender implements Closeable {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final SocketChannel channel;
    private final IO io;
    private final MessageEventListener listener;

    public Sender(SocketChannel channel, IO io, MessageEventListener listener) {
        this.channel = channel;
        this.io = io;
        this.listener = listener;
    }

    public void send(MqttWireMessage message) throws IOException {
        if(isClosed.get()){
            throw new IOException("Current channel is closed!");
        }

        callback.setMessage(message);
        io.registerOutput(channel, callback);
    }

    @Override
    public void close() throws IOException {
        if(isClosed.compareAndSet(false, true)) {
            // 解除注册
            io.unRegisterOutput(channel);
            // 关闭 channel
            channel.close();
        }
    }

    private final OutputMessageCallback callback = new OutputMessageCallback();

    private class OutputMessageCallback implements Runnable {
        /**
         * 需要发送的消息
         */
        private MqttWireMessage message;

        public OutputMessageCallback() {
        }

        public void setMessage(MqttWireMessage message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info("output message runnable run");
            output(message);
        }

        private void output(MqttWireMessage message) {
            if(isClosed.get()) {
                log.warn("消息发送者已处于关闭状态，该消息发送失败，请重新发送!! message:{}", message);
                return;
            }

            // 消息开始发送回调
            listener.onDeliveryStarted(message);
            try {
                // 1.包装消息为 ByteBuffer
                // TODO 此处消息发送缓存区的大小需要解决
                ByteBuffer buffer = ByteBuffer.allocate(128);

                // 2.将缓冲区内容写入到客户端 channel 中
                int len = channel.write(buffer);
                if(len > 0) {
                    // 消息发送完成回调
                    listener.onDeliveryCompleted(message);
                }else {
                    // 消息发送失败回调
                    listener.onDeliveryFailed(message);
                    throw new IOException("Cannot write any data!");
                }
            } catch (IOException e) {
                listener.onDeliveryFailed(message);
                log.error("消息发送失败!! {}", e);
            }
        }
    }
}
