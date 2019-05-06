package com.crossoverjie.distributed.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLimit {

	/**
     * Error code
     * @return
     * code
     */
    int errorCode() default 500;
}
