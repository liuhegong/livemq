package cc.livemq.core.mqtt.exception;

import lombok.Getter;

/**
 *
 */
@Getter
public class MqttException extends Exception {
    public static final int CODE_EXCEPTION = 0x00;

    private int code;
    private Throwable cause;

    public MqttException(Throwable cause) {
        super();
        this.code = CODE_EXCEPTION;
        this.cause = cause;
    }

    public MqttException(int code, Throwable cause) {
        super();
        this.code = code;
        this.cause = cause;
    }

    public MqttException(String message) {
        super();
        this.code = CODE_EXCEPTION;
        this.cause = message == null ? new Exception() : new Exception(message);
    }
}
