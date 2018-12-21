package com.cy.sdkstrategy_master.http;//package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public abstract class BaseRequest<T extends BaseRequest> {
    protected String baseUrl;
    protected String url;
    protected String method;
    protected HttpParams httpParams = new HttpParams();//添加的param

    private Callback callback;

    public BaseRequest(String url, String method) {
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

    public void execute(Callback callback) {
        this.callback = callback;
        new CallImpl(generateRequest()).execute(callback);

    }

    /**
        * 根据不同的请求方式，
        */
    public abstract Request generateRequest();


}
