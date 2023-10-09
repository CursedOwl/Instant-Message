package com.im.server;

import com.im.server.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @PostConstruct
    public void launch() {
        new Thread(()->{
            NettyServer nettyServer = new NettyServer();
            nettyServer.launch(8081);
        }).start();
    }

}
