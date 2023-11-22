package com.im.deal.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedBag {

    private Long id;

    private Long account;

    private Long to;

    @JsonProperty("isGroup")
    private Boolean isGroup;

    private Double amount;

    private Integer people;

    public RedBag(){}

    public RedBag(Long id, Long account, Long to, Boolean isGroup, Double amount, Integer people) {
        this.id = id;
        this.account = account;
        this.to = to;
        this.isGroup = isGroup;
        this.amount = amount;
        this.people = people;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "RedBag{" +
                "id=" + id +
                ", account=" + account +
                ", to=" + to +
                ", isGroup=" + isGroup +
                ", amount=" + amount +
                ", people=" + people +
                '}';
    }
}
