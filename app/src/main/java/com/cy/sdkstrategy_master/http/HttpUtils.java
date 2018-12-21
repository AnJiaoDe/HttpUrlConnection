package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class HttpUtils {

//    private static final String[] methods = {
    ////                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
////        };
    private static HttpUtils httpUtils;

    private HttpUtils() {
    }


    public static HttpUtils getInstance() {
        if (httpUtils == null) httpUtils = new HttpUtils();
        return httpUtils;
    }

    /**
     * get请求
     */
    public GetRequest get(String url) {
        return new GetRequest(url,"GET");
    }



}
