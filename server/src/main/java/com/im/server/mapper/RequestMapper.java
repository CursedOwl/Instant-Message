package com.im.server.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RequestMapper {
    public void insertRequest(Integer id, Integer friendId);
}
