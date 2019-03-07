package cc.livemq.core.mqtt.wire;

/**
 * QoS 2 消息发布完成报文 (QoS 2 第四步)(客户端 <=> 服务端)
 */
public final class MqttPubComp extends MqttAck {

    public MqttPubComp() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PUBCOMP);
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
