package com.im.deal;

import com.google.gson.Gson;
import com.im.deal.entity.RedBag;
import com.im.deal.mapper.RedBagMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class DealApplicationTests {

    private final Gson gson=new Gson();


    @Test
    void contextLoads() {
        RedBag redBag=new RedBag(1L,1L,1L,true,1.0,1);
        System.out.println(gson.toJson(redBag));
    }

}
