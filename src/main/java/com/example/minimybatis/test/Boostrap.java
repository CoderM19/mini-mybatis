package com.example.minimybatis.test;

import com.example.minimybatis.builder.AnnotationMapperBuilder;
import com.example.minimybatis.interceptor.LogInterceptor;
import com.example.minimybatis.mapping.MappedStatement;
import com.example.minimybatis.session.Configuration;
import com.example.minimybatis.session.SqlSession;
import com.example.minimybatis.session.SqlSessionFactory;
import com.example.minimybatis.session.SqlSessionFactoryBuilder;

import java.util.List;

public class Boostrap {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setDriver("com.mysql.cj.jdbc.Driver");
        configuration.setUrl("jdbc:mysql://localhost:3306/test");
        configuration.setUsername("root");
        configuration.setPassword("MESSIted@123");
        configuration.addInterceptor(new LogInterceptor());

        AnnotationMapperBuilder builder = new AnnotationMapperBuilder(UserMapper.class);
        List<MappedStatement> mappedStatements = builder.parseMappedStatements();

        for (MappedStatement ms : mappedStatements) {
            configuration.addMappedStatement(ms);
        }
        SqlSession sqlSession = new SqlSession(configuration);

        try {
            sqlSession.beginTransaction();

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            User user = userMapper.getUserById(1);
            System.out.println("查询结果：" + user);

            if (user != null) {
                user.setUsername("Updated Username");
                int updated = userMapper.updateUser(user);
                System.out.println("更新行数：" + updated);

                User newUser = new User();
                newUser.setUsername("Test User");
                newUser.setEmail("test@example.com");
                int inserted = userMapper.insertUser(newUser);
                System.out.println("插入行数：" + inserted);
            }

            sqlSession.commit();
            System.out.println("事务提交成功！");
        } catch (Exception e) {
            sqlSession.rollback();
            System.out.println("事务已回滚：" + e.getMessage());
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }



    }

    static void main1() {
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
