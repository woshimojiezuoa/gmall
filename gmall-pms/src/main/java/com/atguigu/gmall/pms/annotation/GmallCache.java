package com.atguigu.gmall.pms.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    //缓存前缀
    String prefix() default "";
    //缓存过期时间，以分为单位
     int timeout() default 5;
    //防止雪崩的随机数范围
    int random() default 500;
}
