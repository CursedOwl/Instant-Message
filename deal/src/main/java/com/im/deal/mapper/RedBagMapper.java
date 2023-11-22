package com.im.deal.mapper;

import com.im.deal.entity.RedBag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface RedBagMapper {

    void insertRedBag(RedBag redBag);
}
