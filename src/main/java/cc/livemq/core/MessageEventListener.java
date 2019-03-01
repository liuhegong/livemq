package cc.livemq.core;

import cc.livemq.core.wire.MqttWireMessage;

/**
 * 消息生命周期监听
 */
public interface MessageEventListener {

    /**
     * 接收到新消息时回调
     * @param message
     */
    void onMessageArrived(MqttWireMessage message);

    /**
     * 当消息开始发送时回调
     * @param message
     */
    void onDeliveryStarted(MqttWireMessage message);

    /**
     * 当消息发送完成时回调
     * @param message
     */
    void onDeliveryCompleted(MqttWireMessage message);

    /**
     * 当消息发送失败时回调
     * @param message
     */
    void onDeliveryFailed(MqttWireMessage message);
}
