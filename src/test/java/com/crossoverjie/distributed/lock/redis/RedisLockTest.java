package com.crossoverjie.distributed.lock.redis;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;


public class RedisLockTest {


    @InjectMocks
    private RedisLock redisLock;

    @Mock
    private JedisCluster jedisCluster;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);
        redisLock.setJedis(jedisCluster);

        //redisLock = new RedisLock();
        //HostAndPort hostAndPort = new HostAndPort("10.19.13.51", 7000);
        //JedisCluster jedisCluster = new JedisCluster(hostAndPort);
    }

    @Test
    public void tryLock() throws Exception {
        String key = "test";
        String request = UUID.randomUUID().toString();
        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn("OK");

        boolean locktest = redisLock.tryLock(key, request);
        System.out.println("locktest=" + locktest);

        Assert.assertTrue(locktest);
    }

    @Test
    public void tryLock2() throws Exception {
        boolean locktest = redisLock.tryLock("test", UUID.randomUUID().toString(), 10 * 1000);
        System.out.println("locktest=" + locktest);
    }

    @Test
    public void lock() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn("OK");

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
        System.out.println("lock success expire=" + (end - start) + " lock = " + lock);
    }

    @Test
    public void unlock() throws Exception {
        boolean locktest = redisLock.unlock("test", "ec8ebca0-14ba0-4b23-99a8-b35fbba3629e");
        System.out.println("locktest=" + locktest);
    }

}