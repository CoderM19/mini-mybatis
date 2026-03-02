package com.example.minimybatis.executor;

import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.session.Configuration;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

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
            //1.准备sql语句
            String sql = ms.getSql();
            ps = connection.prepareStatement(sql);
            //2.设置参数
            if (parameter != null && parameter.length > 0) {
                ps.setString(1, parameter[0].toString());
            }
            //3.执行查询
            rs = ps.executeQuery();

            //4.处理结果集
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
