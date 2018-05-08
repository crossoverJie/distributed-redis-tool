package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.constant.RedisToolsConstant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 27/04/2018 17:19
 * @since JDK 1.8
 */
public class RedisLimitJedisClusterJunitTest {

    private RedisLimit redisLimit;

    @Mock
    private JedisCluster jedisCluster;


    @Mock
    private JedisConnectionFactory jedisConnectionFactory ;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);

        redisLimit = new RedisLimit.Builder(jedisConnectionFactory, RedisToolsConstant.CLUSTER)
                .limit(100)
                .build();

    }

    @Test
    public void limit() {
        RedisClusterConnection clusterConnection = new JedisClusterConnection(jedisCluster);
        Mockito.when(jedisConnectionFactory.getClusterConnection()).thenReturn(clusterConnection);

        jedisCluster = (JedisCluster)clusterConnection.getNativeConnection();
        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(0L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertFalse(limit);

    }

    @Test
    public void limitTrue() {

        RedisClusterConnection clusterConnection = new JedisClusterConnection(jedisCluster);
        Mockito.when(jedisConnectionFactory.getClusterConnection()).thenReturn(clusterConnection);

        jedisCluster = (JedisCluster)clusterConnection.getNativeConnection();
        Mockito.when(jedisCluster.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(1L) ;

        boolean limit = redisLimit.limit();
        System.out.println("limit=" + limit);
        Mockito.verify(jedisCluster).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
        Assert.assertTrue(limit);

    }
}
