package cc.livemq.core.mqtt.wire;

/**
 * QoS 2 发布释放报文 (QoS 2 第三步)(客户端 <=> 服务端)
 */
public final class MqttPubRel extends MqttAbstractMessage {

    public MqttPubRel() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PUBREL);
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
