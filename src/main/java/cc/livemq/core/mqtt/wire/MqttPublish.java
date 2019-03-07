package cc.livemq.core.mqtt.wire;

import lombok.Data;

/**
 * 发布消息报文 (客户端 <=> 服务端)
 */
@Data
public final class MqttPublish extends MqttAbstractMessage {

    private int qos;

    public MqttPublish() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PUBLISH);
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
        return getQos() > 0;
    }
}
