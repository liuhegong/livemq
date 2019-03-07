package cc.livemq.core.mqtt.wire;

/**
 * 取消订阅报文确认报文 (服务端 -> 客户端)
 */
public final class MqttUnsubAck extends MqttAck {

    public MqttUnsubAck() {
        super(MqttAbstractMessage.MESSAGE_TYPE_UNSUBACK);
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
