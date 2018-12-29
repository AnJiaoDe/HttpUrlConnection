package com.ly.http;

import android.os.Handler;
import android.os.Looper;

import com.ly.http.utils.LogUtils;

import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class HttpUtils {

    private static final String[] METHODS = {
            "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };
    private HttpUtils httpUtils;
    private Handler handler_deliver;              //用于在主线程执行的调度器

    private Vector<Call> vector_call;//线程安全


    private HttpUtils() {
        handler_deliver = new Handler(Looper.getMainLooper());
        vector_call = new Vector<>();

        // 全局默认信任所有https域名 或 仅添加信任的https域名
        // 使用RequestParams#setHostnameVerifier(...)方法可设置单次请求的域名校验
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    /* 此处使用一个内部类来维护单例 */
    private static class HttpUtilsFactory {
        private static HttpUtils instance = new HttpUtils();
    }

    /* 获取实例 */
    public static HttpUtils getInstance() {

        return HttpUtilsFactory.instance;
    }

    public Handler getHandler_deliver() {
        return handler_deliver;
    }

    /**
     * get请求
     */
    public GetRequestGenerator get(String url) {
        return new GetRequestGenerator(url, METHODS[0]);
    }

    public void cancelByTag(Object tag) {

        if (tag==null)return;
        for (Call call : vector_call) {
            if (call.getRequest().getTag().equals(tag)) {
                call.cancel();
                LogUtils.log("取消了对应的tag的网络请求",call.getRequest().getTag());
                vector_call.remove(call);
                break;
            }
        }

    }

    public void cancelAll() {

        for (Call call : vector_call) {
            call.cancel();
            LogUtils.log("取消了对应的tag的网络请求",call.getRequest().getTag());

        }
        vector_call.clear();

    }

    public void addCall(Call call) {
        vector_call.add(call);
    }

    public void removeCall(Call call){
        vector_call.remove(call);
    }
}
