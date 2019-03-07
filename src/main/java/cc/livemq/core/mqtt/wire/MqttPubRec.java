package cc.livemq.core.mqtt.wire;

/**
 * QoS 2 发布收到报文 (QoS 2 第二步)(客户端 <=> 服务端)
 */
public final class MqttPubRec extends MqttAck {

    public MqttPubRec() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PUBREC);
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
