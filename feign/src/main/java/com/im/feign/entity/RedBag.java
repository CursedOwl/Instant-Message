package com.im.feign.entity;

public class RedBag {

    private Integer account;

    private Integer to;

    private Integer money;

    private Integer people;

    public RedBag(){}

    public RedBag(Integer account, Integer to, Integer money, Integer people) {
        this.account = account;
        this.to = to;
        this.money = money;
        this.people = people;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }
}
