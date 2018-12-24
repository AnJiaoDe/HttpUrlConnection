package com.cy.sdkstrategy_master.http;


import android.os.Build;

import com.cy.sdkstrategy_master.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class CallImpl implements Call {

    private Request request;
    private CallThread callThread;
    private boolean isRunning = true;
    private Callback callback;

    private int responseCode = 0;
    private String responseMsg = "";

    private boolean readComplete = false;//数据是否读取完毕


    static {
        // 全局默认信任所有https域名 或 仅添加信任的https域名
        // 使用RequestParams#setHostnameVerifier(...)方法可设置单次请求的域名校验
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public CallImpl(Request request) {
        this.request = request;
    }


    public void cancel() {
        if (callThread != null) {
            isRunning = false;
            callThread = null;
        }
    }


    @Override
    public void enqueue(final Callback callback) {
        cancel();
        this.callback = callback;
        callThread = new CallThread();
        callThread.start();

    }

    @Override
    public void close() throws IOException {

    }

    private class CallThread extends Thread {

        @Override

        public void run() {
            isRunning = true;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(request.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // try to fix bug: accidental EOFException before API 19
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    httpURLConnection.setRequestProperty("Connection", "close");
                }
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);



//                httpURLConnection.setInstanceFollowRedirects(false);
//
//                if (httpURLConnection instanceof HttpsURLConnection) {
//                    LogUtils.log("instanceof HttpsURLConnection");
//
//                    SSLSocketFactory sslSocketFactory = SSLUtils.getTrustAllSSLSocketFactory();
//                    if (sslSocketFactory != null) {
//                        LogUtils.log("sslSocketFactory != null");
//
//                        ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslSocketFactory);
//                    }
//                }


                // 设置输出可用
                httpURLConnection.setDoOutput(true);
                // 设置请求方式
                LogUtils.log("method",request.getMethod());
                httpURLConnection.setRequestMethod(request.getMethod());
//
//                Field methodField = HttpURLConnection.class.getDeclaredField("method");
//                methodField.setAccessible(true);
//                methodField.set(httpURLConnection, request.getMethod());
                // 设置连接超时
                // httpURLConnection.setConnectTimeout(10000);
                //// 设置读取超时
                //httpURLConnection.setReadTimeout(10000);
                // 设置缓存不可用


//                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");


                // 开始连接
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();
                responseMsg = httpURLConnection.getResponseMessage();
                LogUtils.log("responsmsg", responseMsg);
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                    String result = inputStream2String(inputStream, isRunning);

                    if (readComplete && callback != null) {
                        final String str = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(str);
                                return;
                            }
                        });
                    }
                    if (callback != null) {
                        responseCode = HttpResponseCode.CODE_THREAD_CANCEL;
                        responseMsg = "线程被取消";
                        callFail(responseCode, responseMsg);
                        return;
                    }


                } else {

                    if (callback != null) {
                        callFail(responseCode, responseMsg);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                responseCode = HttpResponseCode.CODE_URL_INVALID;
                responseMsg = "URL不合法";
                callback.onFail(responseCode, responseMsg);

            } catch (ProtocolException e) {
                e.printStackTrace();

                responseCode = HttpResponseCode.CODE_PROTOCOL;
                responseMsg = "网络请求协议不合法";
                callback.onFail(responseCode, responseMsg);

            } catch (IOException e) {
                e.printStackTrace();

                responseCode = HttpResponseCode.CODE_IO_FAILED;
                responseMsg = "网络请求失败,请检查网络";
                callback.onFail(responseCode, responseMsg);


            }
//            catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException e) {
//
//                e.printStackTrace();
//            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void callFail(final int responseCode, final String responseMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFail(responseCode, responseMsg);

            }
        });
    }

    protected void runOnUiThread(Runnable run) {
        HttpUtils.getInstance().getHandler_deliver().post(run);
    }

    /**
     * 字节流转换成字符串
     *
     * @param inputStream
     * @return
     */
    private String inputStream2String(InputStream inputStream, boolean isRunning) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        try {
            while (isRunning && (len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            //中断
            if (inputStream.read(bytes) != -1) {
                readComplete = false;

                return "";
            } else {
                //读取完毕
                readComplete = true;
                return new String(baos.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

}
