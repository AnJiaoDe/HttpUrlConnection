package com.cy.sdkstrategy_master.http;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by cy on 2018/12/24.
 */

public class StringResponseBody implements ResponseBody {
    private byte[] content;
    private String charset = "UTF-8";

    public StringResponseBody(String str, String charset) {
        if (!TextUtils.isEmpty(charset)) this.charset = charset;
        try {
            this.content = str.getBytes(this.charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeTo(OutputStream out) {
        try {
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
