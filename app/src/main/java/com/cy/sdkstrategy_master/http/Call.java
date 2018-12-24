package com.cy.sdkstrategy_master.http;//package com.cy.sdkstrategy_master.http;

import java.io.Closeable;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public interface Call<T> extends Closeable {

    /** 异步回调执行 */
    void enqueue(Callback<T> callback);

}
