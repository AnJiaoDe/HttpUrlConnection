package com.ly.http;//package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public  interface Call<T> {


    /**
     * 异步回调执行
     */
    public abstract void enqueue(Callback<T> callback);

    /**
     *同步请求，开辟了子线程.会阻塞UI线程，但不会引起ANR，注意调用的位置，不要影响UI交互，适合轻量数据

     * @return
     */
    public abstract void sync(Callback<T> callback);

    /**
     * 阻塞请求，未开辟线程，需要手动外面套一个子线程，否则会ANR
     * @param callback
     */
    public abstract void block(Callback<T> callback);

    public abstract void cancel();

    public abstract Request getRequest();
}
