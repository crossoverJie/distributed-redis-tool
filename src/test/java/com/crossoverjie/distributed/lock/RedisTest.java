//package com.crossoverjie.distributed.lock;
//
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import redis.clients.jedis.Jedis;
//
///**
// * Function:
// *
// * @author crossoverJie
// *         Date: 28/03/2018 23:35
// * @since JDK 1.8
// */
//public class RedisTest {
//
//    private RedisLock redisLock;
//
//    @Mock
//    private Jedis jedis;
//
//    @Before
//    public void setBefore() {
//        MockitoAnnotations.initMocks(this);
//        redisLock = new RedisLock.Builder(jedis)
//                .lockPrefix("lock_test")
//                .build();
//
//
//    }
//
//    @Test
//    public void unlock() throws Exception {
//
//        Mockito.when(jedis.eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList())).thenReturn(1L) ;
//
//        boolean locktest = redisLock.unlock("test", "ec8ebca0-14ba0-4b23-99a8-b35fbba3629e");
//
//        Assert.assertTrue(locktest);
//
//        Mockito.verify(jedis).eval(Mockito.anyString(), Mockito.anyList(), Mockito.anyList());
//    }
//}
