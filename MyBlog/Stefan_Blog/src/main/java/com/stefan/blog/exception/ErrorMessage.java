package com.stefan.blog.exception;


/**
 * 错误信息类
 */
public class ErrorMessage {

    //错误编码
    private Integer status;
    //错误地址
    private String errorUrl;
    //错误信息
    private String message;


    public ErrorMessage(Integer status, String errorUrl, String message) {
        this.status = status;
        this.errorUrl = errorUrl;
        this.message = message;
    }

    public ErrorMessage(){

    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "status=" + status +
                ", errorUrl='" + errorUrl + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
