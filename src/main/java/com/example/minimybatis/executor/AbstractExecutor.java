package com.example.minimybatis.executor;

import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.session.Configuration;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractExecutor implements Executor{

    private final static Logger log = Logger.getLogger(String.valueOf(AbstractExecutor.class));

    protected Configuration configuration;

    protected Connection connection;

    public AbstractExecutor(Configuration configuration) {
        this.configuration = configuration;
    }


    /**
     *  模板方法，执行具体的jdbc操作
     * @param ms  封装sql语句和映射信息的对象
     * @param parameter  参数
     * @return  结果列表
     * @param <E> 结果类型泛型
     */
    protected abstract <E> List<E> doQuery(MappedStatement ms, Object[] parameter);


    protected void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.info("关闭连接失败");
        }
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object[] parameter) {
        try {
            this.connection = configuration.getConn();
            return doQuery(ms, parameter);
        }catch (Exception e){
            log.info("获取连接失败");
        }finally {
            closeConnection();
        }
        return null;
    }

    @Override
    public int update(MappedStatement ms, Object[] parameter) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {
     closeConnection();
    }
}
