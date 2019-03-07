package cc.livemq.core.mqtt.wire;

/**
 * 心跳请求报文 (客户端 -> 服务端)
 */
public final class MqttPingReq extends MqttAbstractMessage {

    public MqttPingReq() {
        super(MqttAbstractMessage.MESSAGE_TYPE_PINGREQ);
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
