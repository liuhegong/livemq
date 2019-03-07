package cc.livemq.core.mqtt.wire;

/**
 * QoS 1 消息发布收到确认报文 (QoS 1 第二步)(客户端 <=> 服务端)
 */
public final class MqttPubAck extends MqttAck {

    public MqttPubAck() {
        super(MqttAck.MESSAGE_TYPE_PUBACK);
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
