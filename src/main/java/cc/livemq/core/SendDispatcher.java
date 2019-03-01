package cc.livemq.core;

import cc.livemq.core.wire.MqttWireMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息发送调度器
 */
@Slf4j
public final class SendDispatcher implements Closeable {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final Sender sender;

    /**
     * 非阻塞的线程安全消息队列
     */
    private final Queue<MqttWireMessage> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isSending = new AtomicBoolean();

    public SendDispatcher(Sender sender) {
        this.sender = sender;
    }

    public void send(MqttWireMessage message) throws IOException {
        // offer: 添加一个元素并返回 true, 如果队列已满，则返回 false
        boolean offer = queue.offer(message);
        if(!offer) {
            log.error("消息发送队列已满，该消息发送失败，请重新发送!! message:{}", message);
        }

        if(isSending.compareAndSet(false, true)) {
            sendNextMessage();
        }
    }

    private void sendNextMessage() throws IOException {
        // poll: 移除并返回队列头部的元素，如果队列为空，则返回 null
        MqttWireMessage message = queue.poll();
        if(message == null) {
            log.error("消息发送队列为空，未获取到消息!!");
            return;
        }

        // TODO
        // 通过 Sender 真实发送消息：将消息包装成 ByteBuffer 并写入到客户端 channel 中
        sender.send(message);
    }

    @Override
    public void close() {
        if(isClosed.compareAndSet(false, true)) {
            isSending.set(false);
            log.warn("消息发送队列中消息数量：{}", queue.size());
            // TODO
        }
    }
}
