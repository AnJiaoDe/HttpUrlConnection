package com.ly.http;


/**
 * Created by Administrator on 2018/12/21 0021.
 */

public abstract class Callback<T> {


    /**
     * 对返回数据进行操作的回调，使用enqueue异步请求时，是UI线程
     */
    public abstract void onSuccess(T response);
    /**
     * 进度，使用enqueue异步请求时，是UI线程
     */
    public abstract void onLoding(long current, long length);


    /**
     * 请求失败，响应错误等，都会回调该方法，使用enqueue异步请求时，是UI线程
     */
    public abstract void onFail( String errorMsg);


}
