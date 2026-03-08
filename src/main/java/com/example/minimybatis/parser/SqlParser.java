package com.example.minimybatis.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 解析器 - 解析命名参数并生成参数映射
 */
public class SqlParser {
    
    // 匹配 #{paramName} 格式的正则表达式
    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{([a-zA-Z0-9_]+)\\}");
    
    /**
     * 解析 SQL，提取命名参数并生成参数映射
     * @param sql 原始 SQL（包含 #{paramName}）
     * @return 解析后的 SQL 和参数映射信息
     */
    public ParsedSql parse(String sql) {
        List<String> parameterNames = new ArrayList<>();
        List<Integer> parameterPositions = new ArrayList<>();
        
        StringBuilder parsedSql = new StringBuilder();
        Matcher matcher = PARAM_PATTERN.matcher(sql);
        
        int position = 1; // JDBC 参数位置从 1 开始
        int lastEnd = 0;
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            parameterNames.add(paramName);
            parameterPositions.add(position++);
            
            // 将 #{paramName} 替换为 ?
            matcher.appendReplacement(parsedSql, "?");
            lastEnd = matcher.end();
        }
        
        if (lastEnd < sql.length()) {
            matcher.appendTail(parsedSql);
        }
        
        return new ParsedSql(
            parsedSql.toString(),
            parameterNames,
            parameterPositions
        );
    }
    
    /**
     * 解析后的 SQL 结果
     */
    public static class ParsedSql {
        private final String sql;
        private final List<String> parameterNames;
        private final List<Integer> parameterPositions;
        
        public ParsedSql(String sql, List<String> parameterNames, List<Integer> parameterPositions) {
            this.sql = sql;
            this.parameterNames = parameterNames;
            this.parameterPositions = parameterPositions;
        }
        
        public String getSql() {
            return sql;
        }
        
        public List<String> getParameterNames() {
            return parameterNames;
        }
        
        public List<Integer> getParameterPositions() {
            return parameterPositions;
        }
    }
}
