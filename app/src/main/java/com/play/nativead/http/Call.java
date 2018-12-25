package com.play.nativead.http;//package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public interface Call<T>  {

    /** 异步回调执行 */
    void enqueue(Callback<T> callback);

}
