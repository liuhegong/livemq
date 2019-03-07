package cc.livemq.core.mqtt.wire;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * MQTT Version 3.1.1
 * http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.pdf
 *
 * MQTT 消息顶级报文
 *
 *
 *
 */
public abstract class MqttAbstractMessage {
    public static final byte MESSAGE_TYPE_CONNECT = 1;
    public static final byte MESSAGE_TYPE_CONNACK = 2;
    public static final byte MESSAGE_TYPE_PUBLISH = 3;
    public static final byte MESSAGE_TYPE_PUBACK = 4;
    public static final byte MESSAGE_TYPE_PUBREC = 5;
    public static final byte MESSAGE_TYPE_PUBREL = 6;
    public static final byte MESSAGE_TYPE_PUBCOMP = 7;
    public static final byte MESSAGE_TYPE_SUBSCRIBE = 8;
    public static final byte MESSAGE_TYPE_SUBACK = 9;
    public static final byte MESSAGE_TYPE_UNSUBSCRIBE = 10;
    public static final byte MESSAGE_TYPE_UNSUBACK = 11;
    public static final byte MESSAGE_TYPE_PINGREQ = 12;
    public static final byte MESSAGE_TYPE_PINGRESP = 13;
    public static final byte MESSAGE_TYPE_DISCONNECT = 14;

    /**
     * MQTT 控制报文的类型名称
     * 0, 15: 保留
     */
    public static final String PACKET_NAMES[] = { "Reserved", "CONNECT", "CONNACK", "PUBLISH", "PUBACK", "PUBREC", "PUBREL",
            "PUBCOMP", "SUBSCRIBE", "SUBACK", "UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP", "DISCONNECT",
            "Reserved" };


    // MQTT 报文类型
    private byte type;
    // MQTT 消息唯一标识
    private int msgId;

    public MqttAbstractMessage(byte type) {
        this.type = type;
    }

    /**
     * 固定报头(1 byte)
     * 剩余长度(从第 2 个字节开始)(剩余长度不包括用于编码剩余长度字段本身的长度){
     *      可变报头
     *      负载
     * }
     */

    /**
     * 返回报文字节数组
     * @return
     */
    public byte[] bytes() {
        // 固定报头
        int first = ((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f);

        // 可变报头
        byte[] varHeader = getVariableHeader();
        // 剩余长度
        int remLen = varHeader.length + getPayload().length;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.write(first);
            dos.write(varHeader);
            dos.write(getPayload());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    /**
     * 获取报文固定报头标志位(固定报头低4位)
     * @return
     */
    public abstract byte getMessageInfo();

    /**
     * 获取报文可变报头
     * @return
     */
    public abstract byte[] getVariableHeader();

    /**
     * 获取报文有效荷载
     * @return
     */
    public byte[] getPayload() {
        return new byte[0];
    }

    /**
     * 报文是否包含报文标识符字段
     * 包含报文标识符的报文是:
     * PUBLISH (QoS>0 时), PUBACK, PUBREC, PUBREL, PUBCOMP, SUBSCRIBE, SUBACK, UNSUBSCIBE, UNSUBACK
     * @return
     */
    public abstract boolean isMessageIdRequired();

    /**
     * 返回报文类型
     * @return
     */
    public byte getType() {
        return type;
    }

    /**
     * 返回报文标识符
     * @return
     */
    public int getMsgId() {
        return msgId;
    }
}
