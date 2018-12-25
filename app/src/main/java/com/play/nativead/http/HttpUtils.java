package com.play.nativead.http;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class HttpUtils {

//    private static final String[] methods = {
    ////                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
////        };
    private static HttpUtils httpUtils;
    private Handler handler_deliver;              //用于在主线程执行的调度器

    private HttpUtils() {
        handler_deliver = new Handler(Looper.getMainLooper());
    }


    public static HttpUtils getInstance() {
        if (httpUtils == null) httpUtils = new HttpUtils();

        return httpUtils;
    }

    public Handler getHandler_deliver() {
        return handler_deliver;
    }

    /**
     * get请求
     */
    public GetRequest get(String url) {
        return new GetRequest(url,"GET");
    }




}
