package com.im.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.Socket;

class ServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        try(Socket socket=new Socket("localhost",8081)){
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(0x77);
            dos.writeByte(0xaa);
            byte[] bytes = bos.toByteArray();

            outputStream.write(bytes);
            Thread.sleep(5000);
            byte[] bytes1 = inputStream.readAllBytes();
            for (int i = 0; i < bytes1.length; i++) {
                System.out.printf("0x%02x%n",bytes1[i]);
            }
        }

    }

}
