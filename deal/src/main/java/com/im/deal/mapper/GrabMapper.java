package com.im.deal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GrabMapper {

    public void insertGrab(Long userId,Long redBagId,Double money);
}
