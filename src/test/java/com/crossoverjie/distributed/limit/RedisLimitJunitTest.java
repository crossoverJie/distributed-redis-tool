package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.lock.RedisLock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
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
    private JedisConnectionFactory jedisCluster;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);


        redisLimit = new RedisLimit.Builder<>(jedisCluster)
                .limit(100)
                .build();

    }

    @Test
    public void limit() {

        JedisCluster jedis = (JedisCluster) jedisCluster.getClusterConnection().getNativeConnection();
        Mockito.when((jedis.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList()))).thenReturn(0L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedis).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertFalse(limit);

    }

    @Test
    public void limitTrue() {
        JedisCluster jedis = (JedisCluster) jedisCluster.getClusterConnection().getNativeConnection();
        Mockito.when(jedis.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(1L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedis).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertTrue(limit);

    }
}
