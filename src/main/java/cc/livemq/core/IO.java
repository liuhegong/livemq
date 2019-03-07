package cc.livemq.core;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端 IO 读写操作封装
 */
@Slf4j
public final class IO implements Closeable {
    private final AtomicBoolean isClosed = new AtomicBoolean();
    /** 是否处于读写进行中的锁 */
    private final AtomicBoolean inRead = new AtomicBoolean();
    private final AtomicBoolean inWrite = new AtomicBoolean();

    /** 读写操作的 Selector */
    private final Selector readSelector;
    private final Selector writeSelector;

    /** 读写操作异步任务缓存 */
    private final HashMap<SelectionKey, Runnable> inputTaskMap = new HashMap<>();
    private final HashMap<SelectionKey, Runnable> outputTaskMap = new HashMap<>();

    private final ExecutorService inputPool;
    private final ExecutorService outputPool;

    public IO() throws IOException {
        log.info("初始化全局 IO 读写器.");
        readSelector = Selector.open();
        writeSelector = Selector.open();

        inputPool = Executors.newFixedThreadPool(5,
                new DefaultThreadFactory("read-runnable-thread-"));
        outputPool = Executors.newFixedThreadPool(5,
                new DefaultThreadFactory("write-runnable-thread-"));

        // 启动读写线程
        startRead();
        startWrite();
    }

    private void startRead() {
        Thread thread = new Thread("io- read-thread"){
            @Override
            public void run() {
                while (!isClosed.get()) {
                    try {
                        if(readSelector.select() == 0) {
                            waitSelection(inRead);
                            continue;
                        }else {
                            log.info("有就绪的读事件~~");
                        }

                        Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
                        while(iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();

                            if(key.isValid()) {
                                handleSelectionKey(key, SelectionKey.OP_READ, inputTaskMap, inputPool);
                            }
                        }
                    } catch (IOException e) {
                        log.error("处理读事件失败!! {}", e);
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void startWrite() {
        Thread thread = new Thread("io-write-thread") {
            @Override
            public void run() {
                while (!isClosed.get()) {
                    try {
                        if(writeSelector.select() == 0) {
                            waitSelection(inWrite);
                            continue;
                        }else {
                            log.info("有就绪的写事件~~");
                        }

                        Iterator<SelectionKey> iterator = writeSelector.selectedKeys().iterator();
                        while(iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();

                            if(key.isValid()) {
                                handleSelectionKey(key, SelectionKey.OP_WRITE, outputTaskMap, outputPool);
                            }
                        }
                    } catch (IOException e) {
                        log.error("处理写事件失败!! {}", e);
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void waitSelection(final AtomicBoolean locker) {
        synchronized (locker) {
            if(locker.get()) {
                try {
                    log.info("waitSelection locker:{}", locker.get());
                    locker.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param channel
     * @param runnable
     */
    public void registerInput(SocketChannel channel, Runnable runnable) {
        registerSelection(channel, readSelector, SelectionKey.OP_READ, inRead, inputTaskMap, runnable);
    }

    /**
     *
     * @param channel
     * @param runnable
     */
    public void registerOutput(SocketChannel channel, Runnable runnable) {
        registerSelection(channel, writeSelector, SelectionKey.OP_WRITE, inWrite, outputTaskMap, runnable);
    }

    /**
     *
     * @param channel
     */
    public void unRegisterInput(SocketChannel channel) {
        unRegisterSelection(channel, readSelector, inputTaskMap);
    }

    /**
     *
     * @param channel
     */
    public void unRegisterOutput(SocketChannel channel) {
        unRegisterSelection(channel, writeSelector, outputTaskMap);
    }

    private void unRegisterSelection(SocketChannel channel, Selector selector, HashMap<SelectionKey, Runnable> map) {
        if(channel.isRegistered()) {
            SelectionKey key = channel.keyFor(selector);
            if(key != null) {
                // 1.取消当前所有事件的监听
                key.cancel();
                // 2.移除缓存的异步调度任务
                map.remove(key);
                // 3.通知 selector 重新执行 select() 操作
                selector.wakeup();
            }
        }
    }

    private SelectionKey registerSelection(SocketChannel channel, Selector selector, int registerOps,
                                   AtomicBoolean locker,
                                   HashMap<SelectionKey, Runnable> map, Runnable runnable) {
        char action = '*';
        switch (registerOps) {
            case SelectionKey.OP_READ:
                action = '读';
                break;
            case SelectionKey.OP_WRITE:
                action = '写';
                break;
        }
        log.info("注册 [{}] 事件开始.", action);

        synchronized (locker) {
            // 设定为当前动作状态为进行中
            locker.set(true);

            try {
                // 注册新事件之前必须先唤醒该 selector, 因为该 selector 可能处于 select() 阻塞操作状态中
                selector.wakeup();

                SelectionKey key = null;
                // 判断该 channel 是否已经被注册过
                if(channel.isRegistered()) {
                    // 获取该 channel 在 selector 上注册的所对应的 SelectionKey 并赋值给 key。若无注册关系，则返回 null
                    key = channel.keyFor(selector);
                }

                if(key != null) {
                    /**
                     * key.readyOps(): 返回一个 bit mask, 代表在相应 channel 上可以进行的 IO 操作
                     * key.interestOps(): 返回代表需要 selector 监控的 IO 操作的 bit mask, 如果传递参数则代表设置该 bit mask
                     */
                    key.interestOps(key.readyOps() | registerOps);
                }else {
                    // 注册 selector 监控事件并赋值给 key
                    key = channel.register(selector, registerOps);
                    // 注册回调异步任务
                    map.put(key, runnable);
                }

                log.info("注册 [{}] 事件完成: {}", action, key);
                return key;
            } catch (Exception e) {
                log.error("注册 [{}] 事件失败!! {}", action, e);
                return null;
            } finally {
                log.info("注册 [{}] 事件完成, 解锁并 notify 该 [{}] 锁", action, action);
                // 解锁
                locker.set(false);
                // 此处 try 的原因是有可能当前 locker 的锁并没有在等待，没有处于等待状态，在没有阻塞的情况下如果执行 notify() 操作会有一个异常
                try {
                    // 通知
                    locker.notify();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * 处理某个 SelectionKey 的读写事件
     * @param key
     * @param keyOps
     * @param map
     * @param pool
     */
    private void handleSelectionKey(SelectionKey key, int keyOps, HashMap<SelectionKey, Runnable> map, ExecutorService pool) {
        log.info("处理读写事件...");
        // 1.取消继续对当前处理事件 keyOps 的继续监听
        // 重要: 如果该 key 上注册有多个事件，这里的操作只取消当前事件 keyOps 的监听
        key.interestOps(key.readyOps() & ~keyOps);

        // 2.从任务列表中获取到对应的异步任务
        Runnable runnable = map.get(key);

        // 3.异步调度任务
        if(runnable != null && !pool.isShutdown()) {
            pool.execute(runnable);
        }
    }

    @Override
    public void close() throws IOException {
        if(isClosed.compareAndSet(false, true)) {
            inputPool.shutdown();
            outputPool.shutdown();

            inputTaskMap.clear();
            outputTaskMap.clear();

            readSelector.wakeup();
            writeSelector.wakeup();

            readSelector.close();
            writeSelector.close();
        }
    }

    /**
     * 系统的 ThreadFactory
     * @see Executors.DefaultThreadFactory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
