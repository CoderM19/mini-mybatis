package com.example.minimybatis.session;

import com.example.minimybatis.binding.MapperProxy;
import com.example.minimybatis.executor.Executor;
import com.example.minimybatis.executor.SimpleExecutor;
import com.example.minimybatis.mapping.MappedStatement;

import java.lang.reflect.Proxy;
import java.util.List;

public class SqlSession {

    private Configuration configuration;

    private Executor executor;

    public SqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = configuration.newExecutor();
    }

    /**
     *  获取Mapper接口代理对象
     * @param clazz Mapper接口
     * @return Mapper接口代理对象
     * @param <T> Mapper接口泛型
     */
    public <T> T getMapper(Class<T> clazz) {
        return (T) Proxy
                .newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}
                        , new MapperProxy(this,clazz));
    }

    public <E> List<E> selectList(String statement, Object[] parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        return executor.query(ms, parameter);
    }

    public <T> T selectOne(String statement, Object[] parameter) {
       List<T> resultList = selectList(statement, parameter);
       if (resultList != null && resultList.size() == 1){
           return resultList.get(0);
       }
       return null;
    }


}
