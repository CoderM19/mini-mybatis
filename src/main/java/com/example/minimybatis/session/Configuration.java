package com.example.minimybatis.session;

import com.example.minimybatis.executor.Executor;
import com.example.minimybatis.executor.SimpleExecutor;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.plugin.Interceptor;
import com.example.minimybatis.plugin.InterceptorChain;

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

    public Configuration() {
        interceptorChain = new InterceptorChain();
        variables = new Properties();
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
        Executor executor = new SimpleExecutor(this);
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
}
