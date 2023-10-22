package com.im.server.mapper;

import com.im.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FriendMapper {
    public List<User> selectFriends(Integer id,Integer offset);
}
