package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.lock.RedisLock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 27/04/2018 17:19
 * @since JDK 1.8
 */
public class RedisLimitJunitTest {

    private RedisLimit redisLimit;

    @Mock
    private JedisCluster jedisCluster;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);


        redisLimit = new RedisLimit.Builder<>(jedisCluster)
                .limit(100)
                .build();

    }

    @Test
    public void limit() {

        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(0L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertFalse(limit);

    }

    @Test
    public void limitTrue() {

        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(1L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertTrue(limit);

    }
}
