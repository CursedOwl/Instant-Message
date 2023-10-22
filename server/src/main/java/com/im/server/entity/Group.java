package com.im.server.entity;

public class Group {
    private Integer id;

    private Integer account;

    private String name;

    private Integer owner;

    private Integer member;

    public Group() {
    }
    public Group(Integer id, Integer account, String name, Integer owner, Integer member) {
        this.id = id;
        this.account = account;
        this.name = name;
        this.owner = owner;
        this.member = member;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }
}
