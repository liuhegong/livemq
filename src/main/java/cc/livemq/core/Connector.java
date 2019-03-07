package cc.livemq.core;

import cc.livemq.core.mqtt.wire.MqttAbstractMessage;
import cc.livemq.core.mqtt.wire.MqttRandomMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * 客户端连接器
 */
@Slf4j
public class Connector implements Closeable {
    private String key = UUID.randomUUID().toString().replace("-", "");
    private final SocketChannel channel;
    private final Sender sender;
    private final Receiver receiver;
    private final SendDispatcher sendDispatcher;
    private final ReceiveDispatcher receiveDispatcher;

    public Connector(SocketChannel channel) throws IOException {
        log.info("新客户端连接~~");
        // 注: 使用 NIO 必须设置为非阻塞
        channel.configureBlocking(false);
        this.channel = channel;

        Context ctx = Context.get();
        sender = new Sender(channel, ctx.getIo(), listener);
        receiver = new Receiver(channel, ctx.getIo(), listener);

        sendDispatcher = new SendDispatcher(sender);
        receiveDispatcher = new ReceiveDispatcher(receiver);

        // 当有客户端连接并创建该客户端即刻启动该客户端接收数据
        receiveDispatcher.start();
    }

    /**
     * 对外发送消息的统一入口
     * @param message
     */
    public void send(MqttAbstractMessage message) throws IOException {
        sendDispatcher.send(message);
    }

    public String getKey() {
        return key;
    }

    @Override
    public void close() throws IOException {
        receiveDispatcher.close();
        sendDispatcher.close();
        receiver.close();
        sender.close();
        channel.close();
    }

    private MessageEventListener listener = new MessageEventListener() {
        @Override
        public void onMessageArrived(MqttAbstractMessage message) {
            // TODO
            MqttRandomMessage m = (MqttRandomMessage) message;
            log.info("客户端 [{}] 收到消息：{}", key, new String(m.getPayload()));
        }

        @Override
        public void onDeliveryStarted(MqttAbstractMessage message) {
            // TODO
            log.info("客户端 [{}] 发送消息开始：{}", key, message);
        }

        @Override
        public void onDeliveryCompleted(MqttAbstractMessage message) {
            // TODO
            log.info("客户端 [{}] 发送消息结束：{}", key, message);
        }

        @Override
        public void onDeliveryFailed(MqttAbstractMessage message) {
            // TODO
            log.warn("客户端 [{}] 发送消息失败：{}", key, message);
        }
    };
}
