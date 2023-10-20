package com.im.server.message;

public class PublishMessage {
    private Integer from;

    private Integer to;

    private Boolean isPrivate;

    private String message;

    private Long timeStamp;

    public PublishMessage() {
    }

    public PublishMessage(Integer from, Integer to, String message, Long timeStamp) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
