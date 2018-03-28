package com.crossoverjie.distributed.lock.redis;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;

import static org.junit.Assert.*;

public class RedisLockTest {


    private RedisLock redisLock;

    @Before
    public void setBefore() {
        redisLock = new RedisLock();
        HostAndPort hostAndPort = new HostAndPort("10.19.13.51", 7000);
        JedisCluster jedisCluster = new JedisCluster(hostAndPort);
        redisLock.setJedis(jedisCluster);
    }

    @Test
    public void tryLock() throws Exception {
        boolean locktest = redisLock.tryLock("test", UUID.randomUUID().toString());
        System.out.println("locktest=" + locktest);
    }

    @Test
    public void tryLock2() throws Exception {
        boolean locktest = redisLock.tryLock("test", UUID.randomUUID().toString(),10 * 1000);
        System.out.println("locktest=" + locktest);
    }

    @Test
    public void lock() throws Exception {
        long start = System.currentTimeMillis();
        redisLock.lock("test", UUID.randomUUID().toString());
        long end = System.currentTimeMillis();
        System.out.println("lock success expire=" + (end - start));
    }


    @Test
    public void lock2() throws Exception {
        long start = System.currentTimeMillis();
        boolean lock = redisLock.lock("test", UUID.randomUUID().toString(), 100);
        long end = System.currentTimeMillis();
        System.out.println("lock success expire=" + (end - start) + " lock = " +lock) ;
    }

    @Test
    public void unlock() throws Exception {
        boolean locktest = redisLock.unlock("test", "ec8ebca0-4ba0-4b23-99a8-b35fbba3629e");
        System.out.println("locktest=" + locktest);
    }

}