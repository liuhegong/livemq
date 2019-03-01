package cc.livemq.core;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息接收调度器
 */
@Slf4j
public final class ReceiveDispatcher implements Closeable {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private final Receiver receiver;

    public ReceiveDispatcher(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     *
     */
    public void start() throws IOException {
        if(isClosed.get()) {
            log.warn("消息接收调度器已处于关闭状态!!");
            return;
        }
        receiver.receiver();
    }

    /**
     *
     */
    public void stop() {
        if(isClosed.compareAndSet(false, true)) {
            // TODO
        }
    }

    @Override
    public void close() {
        if(isClosed.compareAndSet(false, true)) {
            // TODO
        }
    }
}
