package com.example.minimybatis.transaction;

import com.example.minimybatis.session.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private Configuration configuration;

    private ThreadLocal<Transaction> transactionThreadLocal = new ThreadLocal<Transaction>();

    private boolean defaultAutoCommit = false;

    public TransactionManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public Transaction getTransaction() {
        return transactionThreadLocal.get();
    }

    public Connection getConnection() throws SQLException {
        Transaction transaction = getTransaction();
        if (transaction != null) {
            return transaction.getConnection();
        }
        try {
            Connection connection = configuration.getConn();
            Transaction newTransaction = new Transaction(connection, defaultAutoCommit);
            transactionThreadLocal.set(newTransaction);
            return newTransaction.getConnection();
        } catch (Exception e) {
            throw new SQLException("获取连接失败", e);
        }
    }

    public void beginTransaction() throws SQLException {
        beginTransaction(defaultAutoCommit);
    }

    public void beginTransaction(boolean autoCommit) throws SQLException {
        Transaction transaction = getTransaction();
        if (transaction != null && !transaction.isClosed()) {
            throw new SQLException("已存在活动的事务");
        }

        Connection connection = null;
        try {
            connection = configuration.getConn();
            Transaction newTransaction = new Transaction(connection, autoCommit);
            transactionThreadLocal.set(newTransaction);
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            throw new SQLException("开始事务失败", e);
        }
    }

    public void commit() throws SQLException {
        Transaction transaction = getTransaction();
        if (transaction != null) {
            try {
                transaction.commit();
            } finally {
                if (!transaction.isAutoCommit()) {
                    transaction.close();
                    transactionThreadLocal.remove();
                }
            }
        }
    }

    public void rollback() throws SQLException {
        Transaction transaction = transactionThreadLocal.get();
        if (transaction != null) {
            try {
                transaction.rollback();
            } finally {
                transaction.close();
                transactionThreadLocal.remove();
            }
        }
    }

    public void close() throws SQLException {
        Transaction transaction = transactionThreadLocal.get();
        if (transaction != null) {
            transaction.close();
            transactionThreadLocal.remove();
        }
    }

    public void setDefaultAutoCommit(boolean autoCommit) {
        this.defaultAutoCommit = autoCommit;
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

}
