package com.ly.http.utils;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public interface IOListener<T> {
    public void onCompleted(T result);
    public void onLoding(long current, long length);
    public void onInterrupted();
    public void onFail(String errorMsg);
}
