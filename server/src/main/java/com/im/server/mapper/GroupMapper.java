package com.im.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GroupMapper {
    public void insertGroup(Integer group, Integer member);

}
