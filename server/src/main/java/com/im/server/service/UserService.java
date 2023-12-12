package com.im.server.service;

public interface UserService {

    public boolean login(Long account, String password);

    public boolean register(String name, String password);

    public boolean account(Long account);

    public boolean addMoney(Long account,Double money);

    public boolean deductMoney(Long account,Double money);

    Double money(Long account);
}
