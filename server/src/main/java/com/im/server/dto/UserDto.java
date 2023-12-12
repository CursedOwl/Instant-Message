package com.im.server.dto;

public class UserDto {
    private String name;

    private Integer account;

    public UserDto() {
    }

    public UserDto(String name, Integer account) {
        this.name = name;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }
}
