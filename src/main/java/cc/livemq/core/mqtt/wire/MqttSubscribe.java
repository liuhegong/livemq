package cc.livemq.core.mqtt.wire;

/**
 * 客户端订阅请求报文 (客户端 -> 服务端)
 */
public final class MqttSubscribe extends MqttAbstractMessage {

    public MqttSubscribe() {
        super(MqttAbstractMessage.MESSAGE_TYPE_SUBSCRIBE);
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
