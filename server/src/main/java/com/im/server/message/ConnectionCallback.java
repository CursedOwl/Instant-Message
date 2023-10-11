package com.im.server.message;

import com.im.server.common.LoginConstants;

public class ConnectionCallback {
    private boolean success;

    private byte status;

    private String message;

    public static ConnectionCallback fail(String message,byte status){
        return new ConnectionCallback(false, status, message);
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
