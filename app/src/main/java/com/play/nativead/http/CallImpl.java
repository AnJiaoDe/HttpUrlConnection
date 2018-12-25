package com.play.nativead.http;


import android.os.Build;

import com.play.nativead.http.utils.BitmapUtils;
import com.play.nativead.http.utils.IOListener;
import com.play.nativead.http.utils.IOUtils;
import com.play.nativead.http.utils.LogUtils;
import com.play.nativead.http.utils.SSLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class CallImpl implements Call {

    private Request request;
    private CallThread callThread;
    private Callback callback;

    private int errorCode = 0;
    private String errorMsg = "";

    private IOUtils ioUtils;

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
        this.ioUtils = new IOUtils();
    }


    public void cancel() {
        if (callThread != null) {
            ioUtils.stop();
            callThread = null;
        }
    }


    @Override
    public void enqueue(final Callback callback) {
        cancel();
        if (callback == null) return;
        this.callback = callback;
        callThread = new CallThread();
        callThread.start();

    }

    private class CallThread extends Thread {

        @Override

        public void run() {

            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(request.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // try to fix bug: accidental EOFException before API 19
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    httpURLConnection.setRequestProperty("Connection", "close");
                }
//                httpURLConnection.setReadTimeout(10000);
//                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setInstanceFollowRedirects(false);
                if (httpURLConnection instanceof HttpsURLConnection) {
//                    LogUtils.log("instanceof HttpsURLConnection");

                    SSLSocketFactory sslSocketFactory = SSLUtils.getTrustAllSSLSocketFactory();
                    if (sslSocketFactory != null) {
//                        LogUtils.log("sslSocketFactory != null");

                        ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslSocketFactory);
                    }
                }

                //设置出入可用
                httpURLConnection.setDoInput(true);
                // 设置输出可用
//                httpURLConnection.setDoOutput(true);
                // 设置请求方式
//                LogUtils.log("method",request.getMethod());
                httpURLConnection.setRequestMethod(request.getMethod());
//
//
                // 设置连接超时
                // httpURLConnection.setConnectTimeout(10000);
                //// 设置读取超时
                //httpURLConnection.setReadTimeout(10000);
                // 设置缓存不可用

//                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");


                // 开始连接
                httpURLConnection.connect();

                errorCode = httpURLConnection.getResponseCode();
                errorMsg = httpURLConnection.getResponseMessage();
                LogUtils.log("responsmsg", errorMsg);
                if (errorCode == 200) {
                    inputStream = httpURLConnection.getInputStream();


                    if (callback instanceof StringCallbackImpl) {


                        new IOUtils().read2String(inputStream, "UTF-8", new IOListener<String>() {
                            @Override
                            public void onCompleted(final String str) {
                                        callSuccess(str);
                            }

                            @Override
                            public void onInterrupted() {
                                errorCode = HttpResponseCode.CODE_THREAD_CANCEL;
                                errorMsg = "线程被取消";
                                callFail(errorCode, errorMsg);
                            }
                        });
                    } else if (callback instanceof BitmapCallbackImpl) {
                        new IOUtils().read2ByteArray(inputStream, new IOListener<byte[]>() {
                            @Override
                            public void onCompleted(final byte[] result) {
                                callSuccess(BitmapUtils.decodeBitmapFromBytes(
                                        result, ((BitmapCallbackImpl) callback).getReqWidth(),
                                        ((BitmapCallbackImpl) callback).getReqHeight()));


                            }

                            @Override
                            public void onInterrupted() {
                                errorCode = HttpResponseCode.CODE_THREAD_CANCEL;
                                errorMsg = "线程被取消";
                                callFail(errorCode, errorMsg);
                            }
                        });
                    }


                } else {
                    callFail(errorCode, errorMsg);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                errorCode = HttpResponseCode.CODE_URL_INVALID;
                errorMsg = "URL不合法";
                callback.onFail(errorCode, errorMsg);

            } catch (ProtocolException e) {
                e.printStackTrace();


                // fix: HttpURLConnection not support PATCH method.

                try {
                    Field methodField = HttpURLConnection.class.getDeclaredField("method");
                    methodField.setAccessible(true);
                    methodField.set(httpURLConnection, request.getMethod());
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } finally {
                    errorCode = HttpResponseCode.CODE_PROTOCOL;
                    errorMsg = "网络请求协议不合法";
                    callback.onFail(errorCode, errorMsg);
                }

            } catch (IOException e) {
                e.printStackTrace();

                errorCode = HttpResponseCode.CODE_IO_FAILED;
                errorMsg = "网络请求失败,请检查网络";
                callback.onFail(errorCode, errorMsg);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                IOUtils.close(inputStream);
            }
        }
    }

    protected void callSuccess(final Object response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response);

            }
        });
    }

    protected void callFail(final int ErrorCode, final String ErrorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFail(ErrorCode, ErrorMsg);

            }
        });
    }

    protected void runOnUiThread(Runnable run) {
        HttpUtils.getInstance().getHandler_deliver().post(run);
    }


}
