package cc.livemq.core.mqtt.codec;

import cc.livemq.core.mqtt.exception.MqttException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * MQTT 编解码
 */
public class MqttCodec {
    private MqttCodec() {}

    public static final int MAX_UTF8_LENGTH = 65535;

    /**
     * MQTT UTF8 编码字符串
     * @param content
     * @return
     * @throws MqttException
     */
    public static byte[] encodeUTF8(String content) throws MqttException {
        byte[] payload = content.getBytes(Charset.defaultCharset());
        if(payload.length > MAX_UTF8_LENGTH) {
            throw new MqttException("字符串大小超出限制:" + MAX_UTF8_LENGTH);
        }

        byte msb = (byte) ((payload.length >>> 8) & 0xFF);
        byte lsb = (byte) ((payload.length >>> 0) & 0xFF);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.write(msb);
            dos.write(lsb);
            dos.write(payload);
        } catch (IOException e) {
            throw new MqttException(e);
        }
        return baos.toByteArray();
    }

    /**
     * MQTT UTF8 解码字符串
     * @param bytes
     * @return
     */
    public static String decodeUTF8(byte[] bytes) {
        return null;
    }
}
