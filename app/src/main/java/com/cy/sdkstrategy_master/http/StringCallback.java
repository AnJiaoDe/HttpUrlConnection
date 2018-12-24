package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/22 0022.
 */

public abstract class StringCallback implements Callback<String> {
    @Override
    public abstract void onSuccess(String response);

    @Override
    public abstract void onFail(int code, String msg);
}
