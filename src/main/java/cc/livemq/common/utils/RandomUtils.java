package cc.livemq.common.utils;

import java.util.Random;

/**
 *
 */
public final class RandomUtils {
    private static final Random RANDOM = new Random();
    private RandomUtils() {}

    /**
     * 生成随机数
     * @return
     */
    public static int generator() {
        return RANDOM.nextInt();
    }
}
