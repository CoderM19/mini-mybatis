package com.example.minimybatis.executor;

import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.binding.ParameterHandler;
import com.example.minimybatis.parser.SqlParser;
import com.example.minimybatis.session.Configuration;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleExecutor extends AbstractExecutor{

    public SimpleExecutor(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object[] parameter) {
        List<E> resultList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 1. 解析 SQL，处理命名参数
            SqlParser sqlParser = new SqlParser();
            String originalSql = ms.getSql();
            SqlParser.ParsedSql parsedSql = sqlParser.parse(originalSql);
            // 2. 准备 SQL 语句
            String sql = parsedSql.getSql();
            ps = connection.prepareStatement(sql);
            // 3. 设置参数 - 支持多种参数类型
            setParameters(ps, parameter, parsedSql.getParameterNames(),parsedSql.getParameterPositions());
            // 4. 执行查询
            rs = ps.executeQuery();
            // 5. 处理结果集
            String resultTypeName = ms.getResultType().getName();
            Class<?> resultType = Class.forName(resultTypeName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Object resultObj = resultType.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(columnName);
    
                    Field field = findField(resultType, columnName);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(resultObj, columnValue);
                    }
                }
                resultList.add((E) resultObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    protected int doUpdate(MappedStatement ms, Object[] parameter) {
        PreparedStatement ps = null;
        try{
            SqlParser sqlParser = new SqlParser();
            String originalSql = ms.getSql();
            SqlParser.ParsedSql parsedSql = sqlParser.parse(originalSql);
            String sql = parsedSql.getSql();
            if (!connection.getAutoCommit()){
                connection.setAutoCommit(false);
            }
            ps = connection.prepareStatement(sql);
            setParameters(ps, parameter, parsedSql.getParameterNames(), parsedSql.getParameterPositions());
            return ps.executeUpdate();
        }catch (Exception e){
            throw new RuntimeException("执行 SQL 错误");
        }finally {
            closeResource(ps, null);
        }
    }

    /**
     * 设置参数 - 支持命名参数和多个参数
     */
    private void setParameters(PreparedStatement ps, Object[] parameter, List<String> paramNames, List<Integer> paramPositions) throws Exception {
        if (parameter == null || parameter.length == 0) {
            return;
        }
        // 获取所有参数的 Map 表示
        Map<String, Object> paramMap;
        if (paramNames != null && !paramNames.isEmpty()) {
            paramMap = ParameterHandler.getParameters(parameter);
            if (paramMap.isEmpty() && parameter[0] != null) {
                if (parameter[0] instanceof Object[]) {
                    Object[] params = (Object[]) parameter[0];
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                } else {
                    ps.setObject(1, parameter[0]);
                }
            }
        } else {
            paramMap = ParameterHandler.getParameters(parameter);
        }
        // 根据参数名称设置值
        for (int i = 0; i < paramNames.size(); i++) {
            String pramName = paramNames.get(i);
            Integer position = paramPositions.get(i);
            Object value = paramMap.get(pramName);
            if (value == null && !paramMap.containsKey(pramName)) {
                value = parameter.length > 0 ? parameter[0] : null;
            }
            if (position != null && position > 0) {
                setParameterValue(ps, position, value);
            }
        }
    }
        
    /**
     * 查找参数的 JDBC 位置
     */
    private int findParameterPosition(String paramName) {
        // TODO: 可以从 MappedStatement 的 ParameterMapping 中查找
        // 这里简单实现：按顺序返回位置
        return 1; // 简化版本，实际应该从 ParameterMapping 获取
    }
        
    /**
     * 设置单个参数值，支持多种类型
     */
    private void setParameterValue(PreparedStatement ps, int position, Object value) throws Exception {
        if (value instanceof Integer) {
            ps.setInt(position, (Integer) value);
        } else if (value instanceof Long) {
            ps.setLong(position, (Long) value);
        } else if (value instanceof Double) {
            ps.setDouble(position, (Double) value);
        } else if (value instanceof Float) {
            ps.setFloat(position, (Float) value);
        } else if (value instanceof Boolean) {
            ps.setBoolean(position, (Boolean) value);
        } else if (value instanceof java.util.Date) {
            ps.setTimestamp(position, new java.sql.Timestamp(((java.util.Date) value).getTime()));
        } else if (value instanceof java.sql.Date) {
            ps.setDate(position, (java.sql.Date) value);
        } else if (value instanceof java.sql.Timestamp) {
            ps.setTimestamp(position, (java.sql.Timestamp) value);
        } else {
            ps.setString(position, value.toString());
        }
    }

    private Field findField(Class<?> clazz, String columnName){
        for (Field field : clazz.getDeclaredFields()){
            if (field.getName().equalsIgnoreCase(columnName)){
                return field;
            }
        }
        return null;
    }

    private void closeResource(PreparedStatement ps, ResultSet rs){
        try{
            if (ps != null) ps.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if (rs != null) rs.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
