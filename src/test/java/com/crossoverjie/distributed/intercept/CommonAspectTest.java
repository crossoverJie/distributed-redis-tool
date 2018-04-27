package com.crossoverjie.distributed.intercept;

import com.crossoverjie.distributed.limit.RedisLimit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class CommonAspectTest {

    @InjectMocks
    private CommonAspect commonAspect;

    @Mock
    private RedisLimit redisLimit;

    @Before
    public void setBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test1() {
        try {
            Mockito.when(redisLimit.limit()).thenReturn(true);
            commonAspect.before(null);
            boolean limit = redisLimit.limit();
            System.out.println(limit);
            assertTrue(limit);
            Mockito.verify(redisLimit,Mockito.times(2)).limit();

        } catch (Exception e) {
        }
    }

    @Test
    public void test2() {
        try {
            Mockito.when(redisLimit.limit()).thenReturn(false);
            commonAspect.before(null);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        boolean limit = redisLimit.limit();
        System.out.println(limit);
        assertFalse(limit);
        Mockito.verify(redisLimit,Mockito.times(2)).limit();
    }

}