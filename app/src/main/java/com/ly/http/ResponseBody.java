package com.ly.http;

/**
 * ************************************************************
 * author：cy
 * version：
 * create：2018/12/27 22:15
 * desc：
 * ************************************************************
 */

public class ResponseBody<T> {
    protected int errorCode = 0;
    protected String errorMsg = "";
    protected T body;

    public ResponseBody(int errorCode, String errorMsg, T body) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.body = body;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
