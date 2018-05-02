package com.crossoverjie.distributed.intercept;

import com.crossoverjie.distributed.limit.RedisLimit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Function: general annotation
 *
 * @author crossoverJie
 *         Date: 27/04/2018 15:50
 * @since JDK 1.8
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonAspect {

    private static Logger logger = LoggerFactory.getLogger(CommonAspect.class);

    @Autowired
    private RedisLimit redisLimit ;

    @Pointcut("@annotation(com.crossoverjie.distributed.annotation.CommonLimit)")
    private void check(){}

    @Before("check()")
    public void before(JoinPoint joinPoint) throws Exception {

        if (redisLimit == null) {
            throw new NullPointerException("redisLimit is null");
        }

        boolean limit = redisLimit.limit();
        if (!limit) {
            logger.warn("request has bean limited");
            throw new RuntimeException("request has bean limited") ;
        }

    }
}
