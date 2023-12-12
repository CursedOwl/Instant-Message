package com.im.feign.entity;

import java.util.List;

public class ResponseEntity {


    private Boolean success;
    private String errorMsg;
    private Object data;
    private Integer total;

    public static ResponseEntity ok(){
        return new ResponseEntity(true, null, null, null);
    }
    public static ResponseEntity ok(Object data){
        return new ResponseEntity(true, null, data, null);
    }
    public static ResponseEntity ok(List<?> data, Integer total){
        return new ResponseEntity(true, null, data, total);
    }
    public static ResponseEntity fail(String errorMsg){
        return new ResponseEntity(false, errorMsg, null, null);
    }

    public ResponseEntity(Boolean success, String errorMsg, Object data, Integer total) {
        this.success = success;
        this.errorMsg = errorMsg;
        this.data = data;
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "success=" + success +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                ", total=" + total +
                '}';
    }
}
