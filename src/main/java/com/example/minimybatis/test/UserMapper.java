package com.example.minimybatis.test;

import com.example.minimybatis.annotation.*;

import java.util.List;

public interface UserMapper {

    @Select("select * from user where id = #{id}")
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "username", property = "username"),
            @Result(column = "email", property = "email")
    })
    User getUserById(Integer id);

    @Select("select * from user")
    List<User> getAllUsers();

    @Insert("insert into user(username, email) values(#{username}, #{email})")
    int insertUser(User user);

    @Update("update user set username = #{username}, email = #{email} where id = #{id}")
    int updateUser(User user);

    @Delete("delete from user where id = #{id}")
    int deleteUser(Integer id);
}
