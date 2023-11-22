package com.im.server;

import com.im.feign.client.DealClient;
import com.im.server.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;


@EnableFeignClients(clients = {DealClient.class})
@SpringBootApplication
public class ServerApplication {

    @Autowired
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @PostConstruct
    public void launch() {
        new Thread(()->{
            nettyServer.launch(8081);
        }).start();
    }

}
