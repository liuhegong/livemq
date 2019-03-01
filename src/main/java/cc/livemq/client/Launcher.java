package cc.livemq.client;

import cc.livemq.constant.Constant;
import cc.livemq.core.wire.MqttRandomMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 *
 */
@Slf4j
public final class Launcher {

    public static void main(String[] args) {
        NioClient client = new NioClient();
        try {
            client.connect("127.0.0.1", Constant.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);
            client.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
