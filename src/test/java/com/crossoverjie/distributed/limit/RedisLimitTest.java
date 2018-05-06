package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.constant.RedisToolsConstant;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class RedisLimitTest {

    private static Logger logger = LoggerFactory.getLogger(RedisLimitTest.class);
    private static ExecutorService executorServicePool;


    private static RedisLimit redisLimit;

    private static JedisPool jedisPool;


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

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(10);
        config.setMaxTotal(300);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration() ;
        redisClusterConfiguration.addClusterNode(new RedisNode("10.19.13.51",7000));

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(config) ;
        jedisConnectionFactory.setHostName("47.98.194.60");
        jedisConnectionFactory.setPort(6379);
        jedisConnectionFactory.setPassword("");
        jedisConnectionFactory.setTimeout(100000);
        jedisConnectionFactory.afterPropertiesSet();
        //jedisConnectionFactory.setShardInfo(new JedisShardInfo("47.98.194.60", 6379));

        HostAndPort hostAndPort = new HostAndPort("47.98.194.60", 6379);
        Jedis jedis = new Jedis("47.98.194.60", 6379);
        //JedisCluster jedisCluster = new JedisCluster(hostAndPort);

        redisLimit = new RedisLimit.Builder(jedisConnectionFactory, RedisToolsConstant.SINGLE)
                .limit(5)
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