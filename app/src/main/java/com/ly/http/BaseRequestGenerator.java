package com.ly.http;//package com.cy.sdkstrategy_master.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public abstract class BaseRequestGenerator<T extends BaseRequestGenerator> {
    protected String baseUrl;
    protected String url;
    protected String method;
    protected HttpParams httpParams = new HttpParams();//添加的param

    private Object tag;

    public BaseRequestGenerator(String url, String method) {
        this.url = url;
        this.baseUrl = url;
        this.method = method;
    }

    public T params(String key, Object value) {
        httpParams.put(key, value);
        return (T) this;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public T tag(Object tag) {
        this.tag = tag;
        return (T) this;

    }

    /**
     * 异步请求
     *
     * @param callback
     */
    public void enqueue(Callback callback) {
        if (callback==null)return;
        if (callback instanceof BitmapCallbackImpl){
            Bitmap bitmap=BitmapFactory.decodeFile(((BitmapCallbackImpl) callback).getCachePath());
            if (bitmap!=null&&bitmap.getWidth()>0) {
                callback.onSuccess(bitmap);
                return;
            }
        }
        Call call = new CallEnqueueImpl(generateRequest(tag));
        HttpUtils.getInstance().addCall(call);
        call.enqueue(callback);

    }

    /**
     * 同步请求.阻塞，注意调用的位置，不要影响UI交互，适合轻量数据
     *
     * @return
     */

    public void sync(Callback callback) {
        if (callback==null)return;
        Call call = new CallSyncImpl(generateRequest(tag));
        HttpUtils.getInstance().addCall(call);
        call.sync(callback);
    }
    public void block(Callback callback){
        if (callback==null)return;
        Call call = new CallWaitImpl(generateRequest(tag));
        HttpUtils.getInstance().addCall(call);
        call.block(callback);
    }

    /**
     * 根据不同的请求方式，
     */
    public abstract Request generateRequest(Object tag);


}
