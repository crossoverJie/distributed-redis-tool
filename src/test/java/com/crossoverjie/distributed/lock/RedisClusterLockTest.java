package com.crossoverjie.distributed.lock;

import com.crossoverjie.distributed.constant.RedisToolsConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisCluster;


import java.util.UUID;

public class RedisClusterLockTest {


    private RedisLock redisLock;

    @Mock
    private JedisConnectionFactory jedisConnectionFactory ;

    @Mock
    private JedisCluster jedisCluster;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);

        redisLock = new RedisLock.Builder(jedisConnectionFactory, RedisToolsConstant.CLUSTER)
                .lockPrefix("lock_")
                .sleepTime(100)
                .build();


        RedisClusterConnection clusterConnection = new JedisClusterConnection(jedisCluster);
        Mockito.when(jedisConnectionFactory.getClusterConnection()).thenReturn(clusterConnection);
        jedisCluster = (JedisCluster)clusterConnection.getNativeConnection();

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

        //check
        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void tryLockFalse() throws Exception {
        String key = "test";
        String request = UUID.randomUUID().toString();
        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn(null);

        boolean lock = redisLock.tryLock(key, request);
        Assert.assertFalse(lock);

        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void tryLock2() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn("OK");

        boolean lock = redisLock.tryLock("test", UUID.randomUUID().toString(), 10 * 1000);

        Assert.assertTrue(lock);

        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void tryLock2False() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn(null);

        boolean lock = redisLock.tryLock("test", UUID.randomUUID().toString(), 10 * 1000);

        Assert.assertFalse(lock);

        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    public void lock() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn("OK");

        long start = System.currentTimeMillis();
        redisLock.lock("test", UUID.randomUUID().toString());
        long end = System.currentTimeMillis();
        System.out.println("lock success expire=" + (end - start));

        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
    }


    @Test
    public void lock2() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn("OK");

        long start = System.currentTimeMillis();
        boolean lock = redisLock.lock("test", UUID.randomUUID().toString(), 100);
        long end = System.currentTimeMillis();
        System.out.println("lock success expire=" + (end - start) + " lock = " + lock);

        Assert.assertTrue(lock);

        Mockito.verify(jedisCluster).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());

    }

    @Test
    public void lock2False() throws Exception {

        Mockito.when(jedisCluster.set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn(null);

        long start = System.currentTimeMillis();
        boolean lock = redisLock.lock("test", UUID.randomUUID().toString(), 100);
        long end = System.currentTimeMillis();
        System.out.println("lock success expire=" + (end - start) + " lock = " + lock);

        Assert.assertFalse(lock);

        //check was called 2 times
        Mockito.verify(jedisCluster,Mockito.times(2)).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());

    }

    @Test
    public void unlock() throws Exception {

        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(1L) ;

        boolean locktest = redisLock.unlock("test", "ec8ebca0-14ba0-4b23-99a8-b35fbba3629e");

        Assert.assertTrue(locktest);

        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
    }

    @Test
    public void unlockFalse() throws Exception {

        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(0L) ;

        boolean locktest = redisLock.unlock("test", "ec8ebca0-14ba0-4b23-99a8-b35fbba3629e");

        Assert.assertFalse(locktest);

        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
    }

}