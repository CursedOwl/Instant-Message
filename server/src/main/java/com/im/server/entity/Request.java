package com.im.server.entity;

public class Request {
    private Integer from;

    private Integer to;

    private Integer result;

    public Request() {
    }

    public Request(Integer from, Integer to, Integer result) {
        this.from = from;
        this.to = to;
        this.result = result;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
