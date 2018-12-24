package com.cy.sdkstrategy_master.http;


/**
 * Created by Administrator on 2018/12/21 0021.
 */

public interface Callback<T> {


    /**
     * 对返回数据进行操作的回调，UI线程
     */
    public void onSuccess(T response);

    /**
     * 请求失败，响应错误等，都会回调该方法，UI线程
     */
    public void onFail(int code, String msg);

}
