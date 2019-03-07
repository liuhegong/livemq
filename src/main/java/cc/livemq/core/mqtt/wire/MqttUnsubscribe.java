package cc.livemq.core.mqtt.wire;

/**
 * 客户端取消订阅请求报文 (客户端 -> 服务端)
 */
public final class MqttUnsubscribe extends MqttAbstractMessage {

    public MqttUnsubscribe() {
        super(MqttAbstractMessage.MESSAGE_TYPE_UNSUBSCRIBE);
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
