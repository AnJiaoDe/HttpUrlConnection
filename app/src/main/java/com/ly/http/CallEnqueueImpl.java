package com.ly.http;


import android.graphics.Bitmap;

import com.ly.http.utils.BitmapUtils;
import com.ly.http.utils.IOListener;
import com.ly.http.utils.IOUtils;
import com.ly.http.utils.SSLUtils;

import java.io.File;
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


                // 设置请求方式
                httpURLConnection.setRequestMethod(request.getMethod());
                //设置出入可用
//                httpURLConnection.setDoInput(true);
                // 设置输出可用
//                httpURLConnection.setDoOutput(true);
                // 开始连接
//                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
//                OutputStream outputStream = httpURLConnection.getInputStream();
                if (httpURLConnection.getResponseCode() == 200) {


                    if (callback instanceof StringCallbackImpl) {


                        ioUtils.read2String(httpURLConnection.getContentLength(), inputStream, new IOListener<String>() {
                            @Override
                            public void onCompleted(final String str) {
                                callSuccess(callback, str);
                            }

                            @Override
                            public void onLoding(long current, long length) {
                                callOnLoding(callback, current, length);
                            }

                            @Override
                            public void onInterrupted() {
                                callFail(callback,  "线程被取消");
                            }

                            @Override
                            public void onFail(String errorMsg) {

                                callFail(callback,errorMsg);
                            }
                        });
                    } else if (callback instanceof BitmapCallbackImpl) {

                        final BitmapCallbackImpl bitmapCallback = (BitmapCallbackImpl) callback;

                        if (bitmapCallback.getCachePath()!=null){

                            ioUtils.read2File(bitmapCallback.getCachePath(),httpURLConnection.getContentLength(), inputStream, new IOListener<File>() {
                                @Override
                                public void onCompleted(File result) {
                                    Bitmap bitmap=BitmapUtils.decodeBitmapFromPath(
                                            result.getPath(),bitmapCallback.getReqWidth(),bitmapCallback.getReqHeight());
                                    if (bitmap != null && bitmap.getWidth() > 0) {
                                        callSuccess(callback, bitmap);
                                    } else {
                                        callFail(callback,  "图片下载失败");
                                    }
                                }

                                @Override
                                public void onLoding(long current, long length) {
                                    callOnLoding(callback, current, length);

                                }

                                @Override
                                public void onInterrupted() {
                                    callFail(callback,  "网络请求失败，线程被取消");

                                }

                                @Override
                                public void onFail(String errorMsg) {
                                    callFail(callback,errorMsg);

                                }
                            });
                        }else {

                            ioUtils.read2ByteArray(httpURLConnection.getContentLength(), inputStream, new IOListener<byte[]>() {
                                @Override
                                public void onCompleted(final byte[] result) {

                                    Bitmap bitmap = BitmapUtils.decodeBitmapFromBytes(result, bitmapCallback.getReqWidth(),
                                            bitmapCallback.getReqHeight());

                                    if (bitmap != null && bitmap.getWidth() > 0) {
                                        callSuccess(callback, bitmap);
                                    } else {
                                        callFail(callback,  "图片下载失败");
                                    }

                                }

                                @Override
                                public void onLoding(long current, long length) {

                                    callOnLoding(callback, current, length);

                                }

                                @Override
                                public void onInterrupted() {
                                    callFail(callback, "网络请求失败，线程被取消");
                                }

                                @Override
                                public void onFail(String errorMsg) {
                                    callFail(callback,errorMsg);

                                }
                            });
                        }

                    }


                } else {
                    callFail(callback, httpURLConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                callFail(callback, "网络请求失败，"+e.getMessage());

            } catch (ProtocolException e) {

                try {
                    Field methodField = HttpURLConnection.class.getDeclaredField("method");
                    methodField.setAccessible(true);
                    methodField.set(httpURLConnection, request.getMethod());
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                    callFail(callback,  "网络请求失败，"+e1.getMessage());

                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                    callFail(callback, "网络请求失败，"+e2.getMessage());

                }

            } catch (IOException e) {
                e.printStackTrace();

                callFail(callback, "网络请求失败，"+e.getMessage());

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                IOUtils.close(inputStream);

                HttpUtils.getInstance().removeCall(CallEnqueueImpl.this);
            }
        }
    }

    protected void callSuccess(final Callback callback, final Object response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response);

            }
        });
    }

    protected void callOnLoding(final Callback callback, final long current, final long length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onLoding(current, length);

            }
        });
    }

    protected void callFail(final Callback callback,  final String ErrorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFail( ErrorMsg);

            }
        });
    }

    protected void runOnUiThread(Runnable run) {
        HttpUtils.getInstance().getHandler_deliver().post(run);
    }


}
