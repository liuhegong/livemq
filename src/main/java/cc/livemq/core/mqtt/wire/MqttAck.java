package cc.livemq.core.mqtt.wire;

/**
 * MQTT 顶级确认报文
 */
public abstract class MqttAck extends MqttAbstractMessage {

    public MqttAck(byte type) {
        super(type);
    }
}
