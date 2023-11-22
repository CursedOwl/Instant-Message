package com.im.server.mapper;


import com.im.server.entity.Request;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RequestMapper {
    public void insertRequest(Integer id, Integer friend);

    List<Request> selectRequest(Integer id,Integer offset);

    void updateState(Integer id, Integer friend, int result);
}
