package com.crossoverjie.distributed.intercept;

import com.crossoverjie.distributed.annotation.ControllerLimit;
import com.crossoverjie.distributed.annotation.SpringControllerLimit;
import com.crossoverjie.distributed.limit.RedisLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 05/05/2018 20:38
 * @since JDK 1.8
 */

public class SpringMVCIntercept extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(SpringMVCIntercept.class);

    @Autowired
    private RedisLimit redisLimit;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (redisLimit == null) {
            throw new NullPointerException("redisLimit is null");
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            SpringControllerLimit annotation = method.getMethodAnnotation(SpringControllerLimit.class);
            if (annotation == null) {
                //skip
                return true;
            }

            boolean limit = redisLimit.limit();
            if (!limit) {
                logger.warn(annotation.errorMsg());
                response.sendError(annotation.errorCode(), annotation.errorMsg());
                return false;
            }

        }

        return true;

    }


}
