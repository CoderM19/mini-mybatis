package com.example.minimybatis.test;

import com.example.minimybatis.interceptor.LogInterceptor;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.session.Configuration;
import com.example.minimybatis.session.SqlSession;

public class Boostrap {


    static void main() {
// 1. 初始化配置（模拟从XML读取）
        Configuration configuration = new Configuration();
        configuration.setDriver("com.mysql.cj.jdbc.Driver");
        configuration.setUrl("jdbc:mysql://localhost:3306/test");
        configuration.setUsername("root");
        configuration.setPassword("MESSIted@123");
        configuration.addInterceptor(new LogInterceptor());  // 注册插件

        // 2. 手动注册一个MappedStatement（模拟解析Mapper.xml）
        MappedStatement ms = new MappedStatement();
        ms.setId("com.example.minimybatis.test.UserMapper.getUserById");
        ms.setSql("select * from user where id = ?");
        ms.setResultType(User.class);
        configuration.addMappedStatement(ms);

        // 3. 创建SqlSession
        SqlSession sqlSession = new SqlSession(configuration);

        // 4. 获取Mapper代理
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 5. 调用方法，见证奇迹！
        User user = userMapper.getUserById(1);
        System.out.println(user);
    }

}
