package cc.livemq.server;

import cc.livemq.server.core.NioServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 *
 */
@Slf4j
public final class Launcher {

    public static void main(String[] args) {
        NioServer server = new NioServer();
        try {
            server.start();
        } catch (Exception e) {
            log.error("server start error!! {}", e);
            try {
                server.stop();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        addHook(server);
    }

    private static void addHook(NioServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
                log.info("jvm exit, server stopped.");
            } catch (Exception e) {
                log.error("jvm exit, server stop error!! {}", e);
            }
        }, "Launcher-Hook-Thread"));
    }
}
