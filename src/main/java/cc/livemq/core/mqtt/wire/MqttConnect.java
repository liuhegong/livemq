package cc.livemq.core.mqtt.wire;

/**
 * 客户端请求连接服务端报文 (客户端 -> 服务端)
 */
public final class MqttConnect extends MqttAbstractMessage {

    public MqttConnect() {
        super(MqttAbstractMessage.MESSAGE_TYPE_CONNECT);
    }

    @Override
    public byte getMessageInfo() {
        return (byte) 0;
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
