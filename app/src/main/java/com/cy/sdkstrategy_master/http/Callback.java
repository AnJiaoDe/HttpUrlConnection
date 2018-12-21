package com.cy.sdkstrategy_master.http;


/**
 * Created by Administrator on 2018/12/21 0021.
 */

public abstract class Callback<T> {


    /**
     * 对返回数据进行操作的回调，
     */
    public abstract void onSuccess(String response);

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法
     */
    public abstract void onFail(int code,String msg);

}
