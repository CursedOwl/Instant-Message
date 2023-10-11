package com.im.server.service.impl;

import com.im.server.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean login(String account, String password) {
        return true;
    }

    @Override
    public boolean account(String account) {
        return true;
    }
}
