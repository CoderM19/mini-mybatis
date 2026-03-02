package com.example.minimybatis.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  标注在拦截器上，声明要拦截的目标方法
 */
@Documented
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.TYPE)
public @interface Intercepts {
    Signature[] value();

}
