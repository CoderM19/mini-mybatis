package com.example.minimybatis.binding;

import com.example.minimybatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public class MapperProxy implements InvocationHandler {

    private SqlSession sqlSession;

    private Class<?> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<?> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        String statementId = mapperInterface.getName() + "." + method.getName();
        Class<?> resultType = method.getReturnType();
        if (resultType == void.class || resultType == int.class || resultType == Integer.class ||
                resultType == long.class || resultType == Long.class) {
            return sqlSession.update(statementId, args);
        } else if (Collection.class.isAssignableFrom(resultType)) {
            return sqlSession.selectList(statementId, args);
        } else {
            return sqlSession.selectOne(statementId, args);
        }
    }
}
