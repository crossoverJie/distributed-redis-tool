package com.crossoverjie.distributed.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringControllerLimit {

    /**
     * Error code
     * @return
     */
    int errorCode() default 500;

    /**
     * Error Message
     * @return
     */
    String errorMsg() default "request limited";
}
