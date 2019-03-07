package cc.livemq.core.mqtt.wire;

/**
 * 订阅请求报文确认报文 (服务端-> 客户端)
 */
public final class MqttSubAck extends MqttAck {

    public MqttSubAck() {
        super(MqttAbstractMessage.MESSAGE_TYPE_SUBACK);
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
        return true;
    }
}
