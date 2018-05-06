package com.crossoverjie.distributed.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringControllerLimit {

    /**
     * Error code
     * @return
     * code
     */
    int errorCode() default 500;

    /**
     * Error Message
     * @return
     * message
     */
    String errorMsg() default "request limited";
}
