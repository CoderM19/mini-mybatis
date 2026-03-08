package com.example.minimybatis.session;

import com.example.minimybatis.executor.Executor;
import com.example.minimybatis.executor.SimpleExecutor;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.plugin.Interceptor;
import com.example.minimybatis.plugin.InterceptorChain;
import com.example.minimybatis.transaction.TransactionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {

    private String driver;

    private String url;

    private String username;

    private String password;

    private InterceptorChain interceptorChain;

    private Properties variables;

    private TransactionManager transactionManager;

    private boolean defaultAutoCommit = false;

    public Configuration() {
        interceptorChain = new InterceptorChain();
        variables = new Properties();
        transactionManager = new TransactionManager(this);
        transactionManager.setDefaultAutoCommit(defaultAutoCommit);
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //存储所有的SQL映射，key为statement的id
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();


    public Executor newExecutor() {
        Executor executor = new SimpleExecutor(this, transactionManager);
        return (Executor) interceptorChain.pluginAll(executor);
    }


    public Connection getConn() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public String getVariable(String key) {
      return variables.getProperty(key);
    }

    public void setVariables(String key ,String value) {
        variables.setProperty(key, value);
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
        if (transactionManager != null) {
            transactionManager.setDefaultAutoCommit(defaultAutoCommit);
        }
    }
}
