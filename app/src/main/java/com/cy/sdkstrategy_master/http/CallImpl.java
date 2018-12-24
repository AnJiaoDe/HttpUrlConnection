package com.cy.sdkstrategy_master.http;


import android.os.Build;

import com.cy.sdkstrategy_master.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private Proxy proxy;

    //    static
//    {
//        try
//        {
//            trustAllHttpsCertificates();
//            HttpsURLConnection.setDefaultHostnameVerifier
//                    (
//                            new HostnameVerifier()
//                            {
//                                public boolean verify(String urlHostName, SSLSession session)
//                                {
//                                    return true;
//                                }
//                            }
//                    );
//        } catch (Exception e)  {}
//    }
    public CallImpl(Request request) {
        this.request = request;

        LogUtils.log("url", request.getUrl());
    }

    public CallImpl(Request request, Proxy proxy) {
        this.request = request;
        this.proxy = proxy;
    }

    public void cancel() {
        if (callThread != null) {
            isRunning = false;
            callThread = null;
        }
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void enqueue(final Callback callback) {
        cancel();
        this.callback = callback;
        callThread = new CallThread();
        callThread.start();

    }

    //    public void i(){
//        isRunning = true;
//
//        HttpsURLConnection httpsURLConnection = null;
//        OutputStream outputStream = null;
//
//        try {
//            URL url = new URL(request.getUrl());
//
//            httpsURLConnection = (HttpsURLConnection) url.openConnection();
//            // 设置输入可用
//            httpsURLConnection.setDoInput(true);
//            // 设置输出可用
//            httpsURLConnection.setDoOutput(true);
//            // 设置请求方式
//            httpsURLConnection.setRequestMethod(request.getMethod());
//            // 设置连接超时
//            // httpURLConnection.setConnectTimeout(10000);
//            //// 设置读取超时
//            //httpURLConnection.setReadTimeout(10000);
//            // 设置缓存不可用
//            httpsURLConnection.setUseCaches(false);
//            // 开始连接
//            httpsURLConnection.connect();
//
//            responseCode = httpsURLConnection.getResponseCode();
//            responseMsg = httpsURLConnection.getResponseMessage();
//            if (responseCode == 200) {
//                InputStream inputStream = httpsURLConnection.getInputStream();
//                String result = inputStream2String(inputStream, isRunning);
//
//                if (readComplete && callback != null) {
//                    final String str = result;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onSuccess(str);
//                            return;
//                        }
//                    });
//                }
//                if (callback!=null){
//                    responseCode = HttpResponseCode.CODE_THREAD_CANCEL;
//                    responseMsg = "线程被取消";
//                    callFail(responseCode, responseMsg);
//                    return;
//                }
//
//
//            } else {
//                if (callback != null) {
//                    callFail(responseCode, responseMsg);
//                }
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            responseCode = HttpResponseCode.CODE_URL_INVALID;
//            responseMsg = "URL不合法";
//            callback.onFail(responseCode, responseMsg);
//
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//
//            responseCode = HttpResponseCode.CODE_PROTOCOL;
//            responseMsg = "网络请求协议不合法";
//            callback.onFail(responseCode, responseMsg);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            responseCode = HttpResponseCode.CODE_IO_FAILED;
//            responseMsg = "网络请求失败,请检查网络";
//            callback.onFail(responseCode, responseMsg);
//
//        } finally {
//            if (httpsURLConnection != null) {
//                httpsURLConnection.disconnect();
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    private class CallThread extends Thread {

        @Override
        public void run() {

            isRunning = true;

            HttpURLConnection httpURLConnection = null;
//            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {


                URL url = new URL(request.getUrl());

                Proxy proxy = getProxy();
                if (proxy != null) {
                    httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }

                // try to fix bug: accidental EOFException before API 19
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    httpURLConnection.setRequestProperty("Connection", "close");
                }
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);

                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

                httpURLConnection.setInstanceFollowRedirects(false);
                if (httpURLConnection instanceof HttpsURLConnection) {
                    LogUtils.log("instanceof HttpsURLConnection");

                    SSLSocketFactory sslSocketFactory = SSLUtils.getTrustAllSSLSocketFactory();
                    if (sslSocketFactory != null) {
                        LogUtils.log("sslSocketFactory != null");

                        ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslSocketFactory);
                    }
                }


                // 设置输入可用
                httpURLConnection.setDoInput(true);
                // 设置输出可用
                httpURLConnection.setDoOutput(true);
                // 设置请求方式
//                httpURLConnection.setRequestMethod(request.getMethod());

                Field methodField = HttpURLConnection.class.getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(httpURLConnection, request.getMethod());
                // 设置连接超时
                // httpURLConnection.setConnectTimeout(10000);
                //// 设置读取超时
                //httpURLConnection.setReadTimeout(10000);
                // 设置缓存不可用
                httpURLConnection.setUseCaches(false);


                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");


                // 开始连接
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();
                responseMsg = httpURLConnection.getResponseMessage();
                LogUtils.log("responsmsg",responseMsg);
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


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {

                e.printStackTrace();
            } finally {
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


    private static void trustAllHttpsCertificates()
            throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[1];
        trustAllCerts[0] = new TrustAllManager();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(
                sc.getSocketFactory());
    }

    private static class TrustAllManager
            implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs,
                                       String authType)
                throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] certs,
                                       String authType)
                throws CertificateException {
        }
    }

}
