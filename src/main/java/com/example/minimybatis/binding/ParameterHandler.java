package com.example.minimybatis.binding;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数处理器 - 处理各种类型的参数并提取值
 */
public class ParameterHandler {

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }
    /**
     * 将参数对象转换为 Map，支持多种参数类型
     * @param parameter 参数对象
     * @return 参数名到参数值的映射
     */
    public static Map<String, Object> getParameters(Object parameter) {
        Map<String, Object> paramMap = new HashMap<>();
        
        if (parameter == null) {
            return paramMap;
        }
        
        // 如果是数组，按索引位置存储
        if (isArray(parameter)) {
            Object[] params = (Object[]) parameter;
            if (params.length == 1){
                Object singleParam = params[0];
                if (singleParam == null){
                    paramMap.put("param1",null);
                    paramMap.put("value",null);
                }else if (isSimpleType(singleParam.getClass())){
                    paramMap.put("param1",singleParam);
                    paramMap.put("value",singleParam);
                }else if (singleParam instanceof Map){
                    paramMap.putAll((Map<? extends String, ?>) singleParam);
                }else {
                    extractBeanProperties(singleParam, paramMap);
                }
            }else {
                for(int i = 0; i < params.length; i++){
                    paramMap.put("param" + (i + 1), params[i]);
                }
            }
        } 
        // 如果是 Map，直接使用
        else if (parameter instanceof Map) {
            paramMap.putAll((Map<? extends String, ?>) parameter);
        }
        // 如果是 JavaBean，通过反射获取属性
        else {
            try {
                Class<?> clazz = parameter.getClass();
                for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                    String fieldName = field.getName();
                    String getterName = "get" + capitalize(fieldName);
                    try {
                        java.lang.reflect.Method getter = clazz.getMethod(getterName);
                        Object value = getter.invoke(parameter);
                        paramMap.put(fieldName, value);
                    } catch (NoSuchMethodException e) {
                        // 尝试 is 前缀（针对 boolean 类型）
                        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                            String isGetterName = "is" + capitalize(fieldName);
                            try {
                                java.lang.reflect.Method isGetter = clazz.getMethod(isGetterName);
                                Object value = isGetter.invoke(parameter);
                                paramMap.put(fieldName, value);
                            } catch (NoSuchMethodException ex) {
                                // 忽略
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract properties from parameter object", e);
            }
        }
        
        return paramMap;
    }



    private static boolean isSimpleType(Class<?> clazz){
        return clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == Integer.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Long.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Character.class ||
                clazz == java.util.Date.class ||
                clazz == java.sql.Date.class ||
                clazz == java.sql.Timestamp.class;
    }

    private static void extractBeanProperties(Object parameter, Map<String, Object> paramMap) {
        try {
            Class<?> clazz = parameter.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                String getterName = "get" + capitalize(fieldName);
                try {
                    java.lang.reflect.Method getter = clazz.getMethod(getterName);
                    Object value = getter.invoke(parameter);
                    paramMap.put(fieldName, value);
                } catch (NoSuchMethodException e) {
                    // 尝试 is 前缀（针对 boolean 类型）
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        String isGetterName = "is" + capitalize(fieldName);
                        try {
                            java.lang.reflect.Method isGetter = clazz.getMethod(isGetterName);
                            Object value = isGetter.invoke(parameter);
                            paramMap.put(fieldName, value);
                        } catch (NoSuchMethodException ex) {
                            try {
                                field.setAccessible(true);
                                Object value = field.get(parameter);
                                paramMap.put(fieldName, value);
                            } catch (IllegalAccessError exx) {

                            }
                        }
                    } else {
                        try {
                            field.setAccessible(true);
                            Object value = field.get(parameter);
                            paramMap.put(fieldName, value);
                        } catch (IllegalAccessError exx) {
                        }
                    }
                }
            }
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                extractBeanProperties(superClass, paramMap);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract properties from parameter object", e);
        }
    }

    
    /**
     * 首字母大写
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
