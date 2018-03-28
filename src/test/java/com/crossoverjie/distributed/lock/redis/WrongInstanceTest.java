package com.crossoverjie.distributed.lock.redis;

import junit.framework.Assert;
import org.easymock.internal.RuntimeExceptionWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 28/03/2018 23:35
 * @since JDK 1.8
 */
public class WrongInstanceTest {

    @InjectMocks
    private RedisLock redisLock;

    @Mock
    private ShardedJedis jedis;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);
        redisLock.setJedis(jedis);

    }

    //@Test(expected = RuntimeException.class)
    public void unlock() throws Exception {

        Mockito.when(redisLock.unlock(Mockito.anyString(),Mockito.anyString())).thenThrow(new RuntimeException()) ;
        redisLock.unlock("test", "ec8ebca0-14ba0-4b23-99a8-b35fbba3629e");


    }
}
