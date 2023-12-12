package com.im.server.mapper;


import com.im.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    public void insertUser(User user);

    public Integer selectUserByAccount(Long account);

    public User selectUserByAccountAndPassword(Long account, String password);

    public void updateMoney(Long account,Double amount);

    Double selectMoneyByAccount(Long account);
}
