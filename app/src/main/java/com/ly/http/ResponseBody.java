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
    protected String errorMsg = "";
    protected T body;

    public ResponseBody(String errorMsg, T body) {
        this.errorMsg = errorMsg;
        this.body = body;
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
