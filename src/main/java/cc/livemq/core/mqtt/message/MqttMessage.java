package cc.livemq.core.mqtt.message;

import lombok.Data;

/**
 * 消息的上层封装
 */
@Data
public final class MqttMessage {
    /** 默认消息 QoS 为 1 */
    private static final int MESSAGE_QOS_DEFAULT = 1;
    /** 默认不是重发消息 */
    private static final boolean MESSAGE_DUPLICATE_DEFAULT = false;
    /** 默认消息是不保留到服务端的 */
    private static final boolean MESSAGE_RETAINED_DEFAULT = false;

    // 消息内容
    private byte[] payload;
    // 消息 QoS
    private int qos = MESSAGE_QOS_DEFAULT;
    // 消息重发标志
    private boolean duplicate = MESSAGE_DUPLICATE_DEFAULT;
    // 消息保留标志
    private boolean retained = MESSAGE_RETAINED_DEFAULT;

    public MqttMessage(byte[] payload) {
        this(payload, MESSAGE_QOS_DEFAULT, MESSAGE_DUPLICATE_DEFAULT, MESSAGE_RETAINED_DEFAULT);
    }

    public MqttMessage(byte[] payload, int qos) {
        this(payload, qos, MESSAGE_DUPLICATE_DEFAULT, MESSAGE_RETAINED_DEFAULT);
    }

    public MqttMessage(byte[] payload, int qos, boolean duplicate) {
        this(payload, qos, duplicate, MESSAGE_RETAINED_DEFAULT);
    }

    public MqttMessage(byte[] payload, int qos, boolean duplicate, boolean retained) {
        this.payload = payload;
        this.qos = qos;
        this.duplicate = duplicate;
        this.retained = retained;
    }
}
