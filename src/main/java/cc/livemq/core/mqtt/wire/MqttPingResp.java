package cc.livemq.core.mqtt.wire;

/**
 * 心跳响应报文 (服务端 -> 客户端)
 */
public final class MqttPingResp extends MqttAck {

    public MqttPingResp() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PINGRESP);
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
