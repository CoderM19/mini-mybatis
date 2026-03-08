package com.example.minimybatis.executor;

import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.session.Configuration;
import com.example.minimybatis.transaction.TransactionManager;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractExecutor implements Executor{

    private final static Logger log = Logger.getLogger(String.valueOf(AbstractExecutor.class));

    protected Configuration configuration;

    protected Connection connection;

    private boolean autoCommit = false;

    protected TransactionManager transactionManager;

    public AbstractExecutor(Configuration configuration, TransactionManager transactionManager) {
        this.configuration = configuration;
        this.transactionManager = transactionManager;
    }


    /**
     *  模板方法，执行具体的jdbc操作
     * @param ms  封装sql语句和映射信息的对象
     * @param parameter  参数
     * @return  结果列表
     * @param <E> 结果类型泛型
     */
    protected abstract <E> List<E> doQuery(MappedStatement ms, Object[] parameter);

    protected abstract int doUpdate(MappedStatement ms, Object[] parameter);


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
            this.connection = transactionManager.getConnection();
            return doQuery(ms, parameter);
        }catch (Exception e){
            log.info("获取连接失败");
        }finally {
            if (autoCommit){
                closeConnection();
            }
        }
        return null;
    }

    @Override
    public int update(MappedStatement ms, Object[] parameter) {
        try {
            this.connection = transactionManager.getConnection();
            int result = doUpdate(ms, parameter);
            if (autoCommit){
             commit();
            }
            return result;
        }catch (Exception e){
            log.info("获取连接失败");
            rollback();
        }finally {
            if (autoCommit){
                closeConnection();
            }
        }
        return 0;
    }

    @Override
    public void commit() {
      try {
         transactionManager.commit();
      }catch (Exception e){
          throw new RuntimeException("提交失败");
      }
    }

    @Override
    public void rollback() {
        try {
           transactionManager.rollback();
        }catch (Exception e){
            throw new RuntimeException("回滚失败");
        }
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public void close() {
        try {
            transactionManager.close();
        } catch (Exception e) {
            log.info("关闭事务失败");
        }
    }

    @Override
    public void beginTransaction() {
        try {
            transactionManager.beginTransaction();
        } catch (Exception e) {
            throw new RuntimeException("开始事务失败", e);
        }
    }

    @Override
    public void beginTransaction(boolean autoCommit) {
        try {
            transactionManager.beginTransaction(autoCommit);
        } catch (Exception e) {
            throw new RuntimeException("开始事务失败", e);
        }
    }
}
