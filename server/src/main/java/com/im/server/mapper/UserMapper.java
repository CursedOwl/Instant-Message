package com.im.server.mapper;


import com.im.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    public void insertUser(User user);
}
