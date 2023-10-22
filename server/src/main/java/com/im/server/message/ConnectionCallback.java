package com.im.server.message;

import com.im.server.common.LoginConstants;

public class ConnectionCallback {
    private static boolean success;
    private byte status;

//    正常情况下发的是JWT，其次是报错信息
    private String message;

    private String secret;


    public static ConnectionCallback fail(String message,byte status){
        return new ConnectionCallback(false, status, message);
    }

    public static ConnectionCallback success(String message,byte status){
        return new ConnectionCallback(true, status, message);
    }

    public ConnectionCallback(boolean success, byte status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
