package com.cy.sdkstrategy_master.http;

import java.io.OutputStream;

/**
 * Created by cy on 2018/12/24.
 */

public interface ResponseBody {

    public void writeTo(OutputStream out);
}
