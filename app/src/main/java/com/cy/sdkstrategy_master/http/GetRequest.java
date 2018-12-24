package com.cy.sdkstrategy_master.http;//package com.cy.sdkstrategy_master.http;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class GetRequest extends BaseRequest<GetRequest> {


    public GetRequest(String url, String method) {
        super(url, method);
    }

    @Override
    public Request generateRequest() {
        Request.Builder builder=new Request.Builder();
        url = ParamsUtils.createUrlFromParams(baseUrl, httpParams.getMap_params());
        return builder.get().url(url).build();
    }
}
