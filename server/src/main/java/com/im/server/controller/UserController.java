package com.im.server.controller;


import com.im.server.entity.ResponseEntity;
import com.im.server.request.UserRequest;
import com.im.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public ResponseEntity login(Long account, String password) {
        boolean login = userService.login(account, password);
        if (login) {
            return ResponseEntity.ok();
        } else {
            return ResponseEntity.fail("账号或密码错误");
        }
    }

    @PostMapping("/register")
    public boolean register(String name, String password) {
        userService.register(name, password);
        return true;
    }

    @GetMapping("/addMoney")
    public ResponseEntity addMoney(@RequestBody UserRequest userRequest) {
        log.info("userRequest:{}", userRequest);
        boolean addMoney = userService.addMoney(userRequest.getAccount(), userRequest.getMoney());
        if (addMoney) {
            return ResponseEntity.ok();
        } else {
            return ResponseEntity.fail("未知错误");
        }
    }

    @GetMapping("/deductMoney")
    public ResponseEntity deductMoney(@RequestBody UserRequest userRequest) {
        boolean deductMoney = userService.deductMoney(userRequest.getAccount(), userRequest.getMoney());
        if (deductMoney) {
            return ResponseEntity.ok();
        } else {
            return ResponseEntity.fail("未知错误");
        }
    }

    @GetMapping("/money")
    public ResponseEntity getMoney(@RequestBody UserRequest userRequest) {
        Long account = userRequest.getAccount();
        Double money = userService.money(account);
        if (money != null && money>=0) {
            return ResponseEntity.ok(money);
        } else {
            return ResponseEntity.fail("未知错误");
        }
    }



}


