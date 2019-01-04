package com.ly.http;


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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class CallSyncImpl<T> implements Call<T> {
    private Request request;
    //    protected ExecutorService fixedThreadPool;//线程池
    private IOUtils ioUtils;
    private ResponseBody<T> syncResponseBody;
    private Callback callback;


    public CallSyncImpl(Request request) {
        this.request = request;
    }


    @Override
    public void cancel() {
        if (ioUtils == null) return;
        ioUtils.stop();

    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void enqueue(Callback callback) {

    }

    @Override
    public void block(Callback<T> callback) {

    }

    @Override
    public void sync(Callback<T> callback) {
        if (callback == null) return;
        this.callback = callback;
        this.ioUtils = new IOUtils();

//        fixedThreadPool = Executors.newFixedThreadPool(1);//线程池

        SyncCallable syncCallable = new SyncCallable();
        FutureTask<ResponseBody<T>> futureTask = new FutureTask<>(syncCallable);
        new Thread(futureTask).start();
//        fixedThreadPool.execute(new Thread(futureTask));
        try {
            syncResponseBody = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (syncResponseBody.getErrorCode() == HttpResponseCode.CODE_SUCCESS) {

            callback.onSuccess(syncResponseBody.getBody());
        } else {

            callback.onFail(syncResponseBody.getErrorCode(), syncResponseBody.getErrorMsg());
        }

    }

    private class SyncCallable implements Callable<ResponseBody<T>> {

        @Override
        public ResponseBody<T> call() {

            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(request.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                SSLUtils.trustAllSSL(httpURLConnection);

                //设置出入可用
                httpURLConnection.setDoInput(true);
                // 设置输出可用
                httpURLConnection.setRequestMethod(request.getMethod());

                // 开始连接
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = httpURLConnection.getInputStream();

                    if (callback instanceof StringCallbackImpl) {


                        ioUtils.read2String(inputStream, "UTF-8", new IOListener<String>() {
                            @Override
                            public void onCompleted(final String str) {
                                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_SUCCESS, "请求成功", str);
                            }

                            @Override
                            public void onInterrupted() {
                                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_THREAD_CANCEL, "线程被取消", "");

                            }
                        });
                    } else if (callback instanceof BitmapCallbackImpl) {
                        ioUtils.read2ByteArray(inputStream, new IOListener<byte[]>() {
                            @Override
                            public void onCompleted(final byte[] result) {
                                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_SUCCESS, "请求成功", BitmapUtils.decodeBitmapFromBytes(
                                        result, ((BitmapCallbackImpl) callback).getReqWidth(),
                                        ((BitmapCallbackImpl) callback).getReqHeight()));

                            }

                            @Override
                            public void onInterrupted() {
                                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_THREAD_CANCEL, "线程被取消", "");

                            }
                        });
                    }


                } else {
                    syncResponseBody = new ResponseBody(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage(), "");

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_URL_INVALID, "URL不合法", "");


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
                    syncResponseBody = new ResponseBody(HttpResponseCode.CODE_PROTOCOL, "网络请求协议不合法", "");

                }

            } catch (IOException e) {
                e.printStackTrace();

                syncResponseBody = new ResponseBody(HttpResponseCode.CODE_IO_FAILED, "网络请求失败,请检查网络", "");


            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                IOUtils.close(inputStream);

                HttpUtils.getInstance().removeCall(CallSyncImpl.this);

            }
            return syncResponseBody;
        }
    }


}
