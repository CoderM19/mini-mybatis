package com.example.minimybatis.plugin;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  描述一个具体的拦截点： 类型、方法名、参数类型
 */
@Documented
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.TYPE)
public @interface Signature {
    //目标接口
    Class<?> type();
    //方法名
    String method();
    //方法参数类型
    Class<?>[] args();
}
