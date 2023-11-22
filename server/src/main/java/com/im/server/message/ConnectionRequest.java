package com.im.server.message;

public class ConnectionRequest {
    private Long account;

    private String password;

    private String jwt;

    public ConnectionRequest(){}

    public ConnectionRequest(Long account, String password, String jwt) {
        this.account = account;
        this.password = password;
        this.jwt = jwt;
    }

    @Override
    public String toString() {
        return "ConnectionRequest{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", jwt='" + jwt + '\'' +
                '}';
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
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
