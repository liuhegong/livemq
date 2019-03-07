package cc.livemq.core;

import cc.livemq.core.mqtt.wire.MqttAbstractMessage;

/**
 * 消息生命周期监听
 */
public interface MessageEventListener {

    /**
     * 接收到新消息时回调
     * @param message
     */
    void onMessageArrived(MqttAbstractMessage message);

    /**
     * 当消息开始发送时回调
     * @param message
     */
    void onDeliveryStarted(MqttAbstractMessage message);

    /**
     * 当消息发送完成时回调
     * @param message
     */
    void onDeliveryCompleted(MqttAbstractMessage message);

    /**
     * 当消息发送失败时回调
     * @param message
     */
    void onDeliveryFailed(MqttAbstractMessage message);
}
