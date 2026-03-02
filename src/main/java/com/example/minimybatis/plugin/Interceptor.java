package com.example.minimybatis.plugin;

public interface Interceptor {

    /**
     *  执行拦截逻辑
     * @param invocation 封装了目标方法、参数等信息
     * @return
     * @throws Throwable
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     *  包装目标对象，生成代理
     * @param target 目标对象
     * @return
     */
    Object plugin(Object target);

}
