package com.im.server.service;

public interface UserService {

    public boolean login(String account, String password);

    public boolean account(String account);
}
