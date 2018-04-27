package com.crossoverjie.distributed.limit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class RedisLimitTest {

    private static Logger logger = LoggerFactory.getLogger(RedisLimitTest.class);
    private static ExecutorService executorServicePool;


    private static RedisLimit redisLimit;


    public static void main(String[] args) throws InterruptedException {
        RedisLimitTest redisLimitTest = new RedisLimitTest();
        redisLimitTest.init();
        initThread();

        for (int i = 0; i < 250; i++) {
            executorServicePool.execute(new Worker(i));
        }

        executorServicePool.shutdown();
        while (!executorServicePool.awaitTermination(1, TimeUnit.SECONDS)) {
            logger.info("worker running");
        }
        logger.info("worker over");

    }

    @Before
    public void setBefore() {
        init();

    }

    private void init() {
        HostAndPort hostAndPort = new HostAndPort("10.19.13.51", 7000);
        JedisCluster jedisCluster = new JedisCluster(hostAndPort);

        redisLimit = new RedisLimit.Builder<>(jedisCluster)
                .limit(100)
                .build();

    }

    public static void initThread() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("current-thread-%d").build();
        executorServicePool = new ThreadPoolExecutor(350, 350, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(200), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    }


    private static class Worker implements Runnable {

        private int index;

        public Worker(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            boolean limit = redisLimit.limit();
            if (!limit) {
                logger.info("限流了 limit={},index={}", limit, index);
            } else {
                logger.info("=======index{}===通过=====", index);

            }
        }
    }


}