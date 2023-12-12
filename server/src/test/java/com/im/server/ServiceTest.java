package com.im.server;

import com.im.server.entity.User;
import com.im.server.mapper.UserMapper;
import com.im.server.message.PublishMessage;
import com.im.server.service.MessageService;
import com.im.server.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ServiceTest {


    private MessageService messageService=new MessageServiceImpl();

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
    }

    @Test
    public void test2(){
        User test = new User("Test", 123456L, "123456");
        userMapper.insertUser(test);
    }


}
