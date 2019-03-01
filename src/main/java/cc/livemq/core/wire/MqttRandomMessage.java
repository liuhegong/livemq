package cc.livemq.core.wire;

import lombok.Data;

/**
 * MQTT 测试随机报文
 */
@Data
public class MqttRandomMessage extends MqttWireMessage {
    private byte[] payload;

    public MqttRandomMessage(String message) {
        payload = message.getBytes();
    }
}
