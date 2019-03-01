package cc.livemq.core;

import cc.livemq.core.properties.ConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
@Slf4j
public final class Context {
    private static Context context;
    private final ClassLoader loader;
    private final ConfigurationProperties cfg;
    private final IO io;

    private Context() throws IOException {
        log.info("初始化全局上下文.");
        loader = this.getClass().getClassLoader();
        cfg = loadConfig();
        io = new IO();
    }

    private ConfigurationProperties loadConfig() throws IOException {
        log.info("加载配置文件.");
        // TODO
        InputStream in = loader.getResourceAsStream(ConfigurationProperties._NAME);
        if(in != null) {
            Properties props = new Properties();
            props.load(in);
        }
        return new ConfigurationProperties();
    }

    public static Context get() throws IOException {
        if(context == null) {
            start();
        }
        return context;
    }

    public static Context start() throws IOException {
        if(context == null) {
            synchronized (Context.class) {
                if(context == null) {
                    context = new Context();
                }
            }
        }
        return context;
    }

    public static void stop() throws IOException {
        if(context != null) {
            context.close();
        }
    }

    private void close() throws IOException {
        io.close();
    }

    public IO getIo() {
        return io;
    }
}
