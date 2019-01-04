package com.ly.http;


import android.graphics.Bitmap;

import com.ly.http.utils.BitmapUtils;
import com.ly.http.utils.IOListener;
import com.ly.http.utils.IOUtils;
import com.ly.http.utils.SSLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class CallEnqueueImpl<T> implements Call<T> {

    private Request request;
    private CallThread callThread;
    private Callback callback;
    private IOUtils ioUtils;

    public CallEnqueueImpl(Request request) {
        this.request = request;
    }


    @Override
    public void cancel() {
        if (callThread != null) {
            ioUtils.stop();
            callThread = null;
        }

    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void enqueue(final Callback callback) {
        cancel();
        if (callback == null) return;
        this.callback = callback;
        callThread = new CallThread();
        this.ioUtils = new IOUtils();
        callThread.start();

    }

    @Override
    public void sync(Callback<T> callback) {

    }

    @Override
    public void block(Callback<T> callback) {

    }

    private class CallThread extends Thread {

        @Override

        public void run() {

            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(request.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                SSLUtils.trustAllSSL(httpURLConnection);

                //设置出入可用
                httpURLConnection.setDoInput(true);
                // 设置输出可用
//                httpURLConnection.setDoOutput(true);
                // 设置请求方式
                httpURLConnection.setRequestMethod(request.getMethod());

                // 开始连接
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = httpURLConnection.getInputStream();


                    if (callback instanceof StringCallbackImpl) {


                        ioUtils.read2String(inputStream, "UTF-8", new IOListener<String>() {
                            @Override
                            public void onCompleted(final String str) {
                                callSuccess(str);
                            }

                            @Override
                            public void onInterrupted() {
                                callFail(HttpResponseCode.CODE_THREAD_CANCEL, "线程被取消");
                            }
                        });
                    } else if (callback instanceof BitmapCallbackImpl) {
                        ioUtils.read2ByteArray(inputStream, new IOListener<byte[]>() {
                            @Override
                            public void onCompleted(final byte[] result) {

                                Bitmap bitmap= BitmapUtils.decodeBitmapFromBytes(
                                        result, ((BitmapCallbackImpl) callback).getReqWidth(),
                                        ((BitmapCallbackImpl) callback).getReqHeight());
                                if (((BitmapCallbackImpl) callback).isCache())
                                BitmapUtils.
                            }

                            @Override
                            public void onInterrupted() {
                                callFail(HttpResponseCode.CODE_THREAD_CANCEL, "线程被取消");
                            }
                        });
                    }


                } else {
                    callFail(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                callFail(HttpResponseCode.CODE_URL_INVALID, "URL不合法");

            } catch (ProtocolException e) {

                try {
                    Field methodField = HttpURLConnection.class.getDeclaredField("method");
                    methodField.setAccessible(true);
                    methodField.set(httpURLConnection, request.getMethod());
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } finally {
                    callFail(HttpResponseCode.CODE_PROTOCOL, "网络请求协议不合法");
                }

            } catch (IOException e) {
                e.printStackTrace();

                callFail(HttpResponseCode.CODE_IO_FAILED, "网络请求失败,请检查网络");

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                IOUtils.close(inputStream);

                HttpUtils.getInstance().removeCall(CallEnqueueImpl.this);
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
