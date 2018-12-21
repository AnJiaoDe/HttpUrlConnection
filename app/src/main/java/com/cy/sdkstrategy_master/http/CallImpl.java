package com.cy.sdkstrategy_master.http;


import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class CallImpl implements Call {

    private Request request;

    private Callable<String> callable;
    private boolean isRunning = true;
    private Handler handler;
    public CallImpl(Request request) {
        this.request = request;

        handler=new Handler(Looper.getMainLooper());
    }

    public void cancel() {
        if (callable != null) {
            isRunning = false;
            callable = null;
        }
    }

    @Override
    public void execute(final Callback callback) {
        cancel();
        callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
//                LogUtils.log("callStart",request.getUrl());
                isRunning = true;
                HttpURLConnection httpURLConnection = null;
                OutputStream outputStream = null;

                try {
                    URL url = new URL(request.getUrl());

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    // 设置输入可用
                    httpURLConnection.setDoInput(true);
                    // 设置输出可用
                    httpURLConnection.setDoOutput(true);
                    // 设置请求方式
                    httpURLConnection.setRequestMethod(request.getMethod());
                    // 设置连接超时
//            httpURLConnection.setConnectTimeout(10000);
//            // 设置读取超时
//            httpURLConnection.setReadTimeout(10000);
                    // 设置缓存不可用
                    httpURLConnection.setUseCaches(false);
                    // 开始连接
                    httpURLConnection.connect();

                    // 只有当POST请求时才会执行此代码段
//            if (params != null) {
//                // 获取输出流,connection.getOutputStream已经包含了connect方法的调用
//                outputStream = connection.getOutputStream();
//                StringBuilder sb = new StringBuilder();
//                Set<Map.Entry<String, String>> sets = params.entrySet();
//                // 将Hashmap转换为string
//                for (Map.Entry<String, String> entry : sets) {
//                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//                }
//                String param = sb.substring(0, sb.length() - 1);
//                // 使用输出流将string类型的参数写到服务器
//                outputStream.write(param.getBytes());
//                outputStream.flush();
//            }

                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
//                        LogUtils.log(httpURLConnection.getResponseMessage());
                        InputStream inputStream = httpURLConnection.getInputStream();
                        String result = inputStream2String(inputStream, isRunning);
//                        LogUtils.log("result",result);

                        if (!result.equals("") && callback != null) {
                            callback.onSuccess(result);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(httpURLConnection.getResponseCode(),httpURLConnection.getResponseMessage());
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    callback.onFail(HttpResponseCode.CODE_URL_INVALID,"URL不合法");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                    callback.onFail(HttpResponseCode.CODE_PROTOCOL,"网络请求协议不合法");

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFail(HttpResponseCode.CODE_IO_FAILED,"网络请求失败,请检查网络");

                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };
        FutureTask<String> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();

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
            return new String(baos.toByteArray());
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
