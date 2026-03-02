package com.example.minimybatis.interceptor;

import com.example.minimybatis.executor.Executor;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object[].class})
})
public class LogInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("Before query:{}", invocation.getMethod().getName());
        Object result = invocation.proceed();
        log.info("After query " );
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
