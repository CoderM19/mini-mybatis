package com.example.minimybatis.transaction;


import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

    private Connection connection;

    private boolean autoCommit;

    private boolean isClosed = false;

    public Transaction(Connection connection, boolean autoCommit) throws SQLException {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.isClosed = false;
        if (connection != null){
            connection.setAutoCommit(autoCommit);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() throws SQLException {
        if (connection != null && !isClosed && !autoCommit) {
            connection.commit();
        }
    }

    public void rollback() throws SQLException {
        if (connection != null && !isClosed && !autoCommit) {
            connection.rollback();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !isClosed) {
            isClosed = true;
            connection.close();
        }
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
        if (connection != null && !isClosed) {
            connection.setAutoCommit(autoCommit);
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

}
