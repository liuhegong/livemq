package cc.livemq.core.mqtt.wire;

/**
 * 连接报文确认报文 (服务端 -> 客户端)
 */
public final class MqttConnAck extends MqttAck {

    public MqttConnAck() {
        super(MqttAbstractMessage.MESSAGE_TYPE_CONNACK);
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
