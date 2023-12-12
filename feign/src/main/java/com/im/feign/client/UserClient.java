package com.im.feign.client;


import com.im.feign.entity.ResponseEntity;
import com.im.feign.entity.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="user",url = "http://localhost:8080")
public interface UserClient {

    @GetMapping("/user/addMoney")
    public ResponseEntity addMoney(@RequestBody UserRequest userRequest);

    @GetMapping("/user/deductMoney")
    public ResponseEntity deductMoney(@RequestBody UserRequest userRequest);

    @GetMapping("/user/money")
    public ResponseEntity money(@RequestBody UserRequest userRequest);



}
