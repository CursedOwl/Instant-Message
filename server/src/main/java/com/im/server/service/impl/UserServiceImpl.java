package com.im.server.service.impl;

import com.im.server.entity.User;
import com.im.server.mapper.UserMapper;
import com.im.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private final Random random=new Random();

    @Override
    public boolean login(Long account, String password) {
        return userMapper.selectUserByAccountAndPassword(account,password)!=null;
    }

    @Override
    public boolean register(String name, String password) {
        RLock lock = redissonClient.getLock("lock:account");
        try {
            if (lock.tryLock(10,10, TimeUnit.SECONDS)) {
                while(true){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(random.nextInt(9)+1);
                    for (int i=0;i<7;i++) {
                        stringBuilder.append(random.nextInt(10));
                    }
                    long account = Long.parseLong(stringBuilder.toString());
                    if (userMapper.selectUserByAccount(account)==0) {
                        log.info("Account register:{}",account);
                        User user = new User(name, account, password);
                        userMapper.insertUser(user);
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public boolean account(Long account) {
        return userMapper.selectUserByAccount(account)!=0;
    }

    @Override
    @Transactional
    public boolean addMoney(Long account, Double money) {
        userMapper.updateMoney(account,money);
        return true;
    }

    @Override
    @Transactional
    public boolean deductMoney(Long account, Double money) {
        userMapper.updateMoney(account,-money);
        return true;
    }

    @Override
    public Double money(Long account) {
        return userMapper.selectMoneyByAccount(account);
    }
}
