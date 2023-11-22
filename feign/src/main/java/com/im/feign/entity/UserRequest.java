package com.im.feign.entity;

public class UserRequest {

        private Long account;

        private Double money;

        public UserRequest(){}

    public UserRequest(Long account) {
        this.account = account;
    }

    public UserRequest(Long account, Double money) {
            this.account = account;
            this.money = money;
        }

        public Long getAccount() {
            return account;
        }

        public Double getMoney() {
            return money;
        }

        public void setAccount(Long account) {
            this.account = account;
        }

        public void setMoney(Double money) {
            this.money = money;
        }
}
