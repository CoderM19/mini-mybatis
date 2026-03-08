package com.example.minimybatis.builder;

import com.example.minimybatis.annotation.*;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.mapping.ParameterMapping;
import com.example.minimybatis.parser.SqlParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class AnnotationMapperBuilder {

    private final Class<?> mapperClass;

    public AnnotationMapperBuilder(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public List<MappedStatement> parseMappedStatements() {
        List<MappedStatement> mappedStatements = new ArrayList<>();
        for (Method method : mapperClass.getDeclaredMethods()) {
            MappedStatement ms = parseMethod(method);
            if (ms != null){
                mappedStatements.add(ms);
            }
        }
        return mappedStatements;
    }


    private MappedStatement parseMethod(Method method) {
        try {
            MappedStatement ms = new MappedStatement();

            String statementId = mapperClass.getName() + "." + method.getName();
            ms.setId(statementId);

            Class<?> returnType = method.getReturnType();
            if (returnType != null) {
                ms.setResultType(returnType);
            }

            Class<?>[] methodParameterTypes = method.getParameterTypes();
            if (methodParameterTypes.length > 0) {
                List<Class<?>> parameterTypes = new ArrayList<>();
                for (Class<?> parameterType : methodParameterTypes) {
                    parameterTypes.add(parameterType);
                }
                ms.setParameterTypes(parameterTypes);
            }
            String sql = null;
            String[] sqlArray = null;

            if (method.isAnnotationPresent(Select.class)) {
                Select select = method.getAnnotation(Select.class);
                sqlArray = select.value();
            } else if (method.isAnnotationPresent(Update.class)) {
                Update update = method.getAnnotation(Update.class);
                sqlArray = update.value();
            } else if (method.isAnnotationPresent(Delete.class)) {
                Delete delete = method.getAnnotation(Delete.class);
                sqlArray = delete.value();
            } else if (method.isAnnotationPresent(Insert.class)) {
                Insert insert = method.getAnnotation(Insert.class);
                sqlArray = insert.value();
            }

            if (sqlArray != null && sqlArray.length > 0) {
                StringBuilder sqlBuilder = new StringBuilder();
                for (String s : sqlArray) {
                    sqlBuilder.append(s.trim()).append(" ");
                }
                sql = sqlBuilder.toString().trim();
            }

            if (sql != null) {
                ms.setSql(sql);

                SqlParser sqlParser = new SqlParser();
                SqlParser.ParsedSql parsedSql = sqlParser.parse(sql);

                List<ParameterMapping> parameterMappings = new ArrayList<>();
                for (String paramName : parsedSql.getParameterNames()) {
                    ParameterMapping parameterMapping = new ParameterMapping();
                    parameterMapping.setName(paramName);
                    parameterMappings.add(parameterMapping);
                }
                ms.setParameterMappings(parameterMappings);
            }

            parseResults(method, ms);
            return ms;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseResults(Method method, MappedStatement ms) {
        if (method.isAnnotationPresent(Result.class)) {
            Results results = method.getAnnotation(Results.class);
            Result[] resultAnnotations = results.value();
            if (resultAnnotations != null && resultAnnotations.length > 0) {
                ms.setResultType(method.getReturnType());
            }
        }
    }


}
