package com.ly.http;//package com.cy.sdkstrategy_master.http;


import com.ly.http.utils.ParamsUtils;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class GetRequestGenerator extends BaseRequestGenerator<GetRequestGenerator> {


    public GetRequestGenerator(String url, String method) {
        super(url, method);
    }

    @Override
    public Request generateRequest(Object tag) {
        Request.Builder builder=new Request.Builder();
        url = ParamsUtils.createUrlFromParams(baseUrl, httpParams.getMap_params());
        return builder.get().tag(tag).url(url).build();
    }
}
