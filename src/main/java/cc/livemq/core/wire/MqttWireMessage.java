package cc.livemq.core.wire;

import cc.livemq.common.utils.RandomUtils;

/**
 * MQTT 消息顶级报文
 */
public abstract class MqttWireMessage {
    // MQTT 消息唯一标识
    protected int msgId;

    public MqttWireMessage() {
        this.msgId = RandomUtils.generator();
    }

}
