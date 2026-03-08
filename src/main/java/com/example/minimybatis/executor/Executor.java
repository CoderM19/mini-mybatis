package com.example.minimybatis.executor;

import com.example.minimybatis.mapping.MappedStatement;

import java.util.List;

/**
 * 执行器接口，定义数据库操作的基本方法
 */
public interface Executor {
    /**
     *  查询方法，返回结果列表
     * @param ms 封装sql语句和映射信息的对象
     * @param parameter 参数数组
     * @return  结果列表
     * @param <E>  结果类型泛型
     */
    <E> List<E> query(MappedStatement ms, Object[] parameter);

    /**
     *  更新方法
     * @param ms 封装sql语句和映射信息的对象
     * @param parameter 参数数组
     * @return 更新记录数
     */
    int update(MappedStatement ms, Object[] parameter);

    /**
     *  提交事务
     */
    void commit();

    /**
     *  回滚事务
     */
    void rollback();

    /**
     *  关闭连接
     */
    void close();

    void beginTransaction();

    void beginTransaction(boolean autoCommit);
}
