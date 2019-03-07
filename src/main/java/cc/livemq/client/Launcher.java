package cc.livemq.client;

import cc.livemq.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 *
 */
@Slf4j
public final class Launcher {

    public static void main(String[] args) {
        NioClient client = null;
        try {
            client = new NioClient();
            client.connect("127.0.0.1", Constant.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(client != null) {
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
