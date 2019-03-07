package cc.livemq.core.mqtt.wire;

import lombok.Data;

/**
 * MQTT 测试随机报文
 */
@Data
public class MqttRandomMessage extends MqttAbstractMessage {
    private byte[] payload;

    public MqttRandomMessage(String message) {
        super((byte) 99);
        payload = message.getBytes();
    }

    @Override
    public byte getMessageInfo() {
        return 0;
    }

    @Override
    public byte[] getVariableHeader() {
        return new byte[0];
    }

    @Override
    public boolean isMessageIdRequired() {
        return false;
    }
}
