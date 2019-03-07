package cc.livemq.core.mqtt.wire;

/**
 * 客户端断开连接报文 (客户端 -> 服务端)
 */
public final class MqttDisconnect extends MqttAbstractMessage {

    public MqttDisconnect() {
        super(MqttAbstractMessage.MESSAGE_TYPE_DISCONNECT);
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
