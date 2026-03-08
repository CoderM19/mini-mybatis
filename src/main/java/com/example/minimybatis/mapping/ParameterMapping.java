package com.example.minimybatis.mapping;

/**
 * 参数映射 - 描述 SQL 中参数的详细信息
 */
public class ParameterMapping {

    private String name;

    private Class<?> javaType;

    private String jdbcType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
