package com.im.server.entity;

public class ConnectionRequest {

    private String account;

    private String password;

    private String jwt;

    public ConnectionRequest(){}

    public ConnectionRequest(String account, String password, String jwt) {
        this.account = account;
        this.password = password;
        this.jwt = jwt;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
