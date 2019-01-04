package com.ly.http;//package com.cy.sdkstrategy_master.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class HttpParams {

    /**
     * 普通的键值对参数
     */
    private Map<String, Object> map_params = new HashMap<>();

    public void put(String key, Object value) {
        map_params.put(key,value);
    }

    public Map<String, Object> getMap_params() {
        return map_params;
    }
}
