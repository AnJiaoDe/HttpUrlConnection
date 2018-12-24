package com.cy.sdkstrategy_master.http;

import android.graphics.Bitmap;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2018/11/26 0026.
 */


/**
 * 有返回值的runnable
 */
public class ImageCallable implements Callable<Bitmap> {

    private boolean isDownloadRunning=true;
    private String imgURL;
    private int width, height;

    public ImageCallable(String imgURL, int width, int height) {
        this.imgURL = imgURL;
        this.width = width;
        this.height = height;
    }

    public void stop(){
        isDownloadRunning=false;
    }
    @Override
    public Bitmap call() throws Exception {
        if (imgURL==null&&!isDownloadRunning) return null;
        try {
            URL url = new URL(imgURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //这里就简单的设置了网络的读取和连接时间上线，如果时间到了还没成功，那就不再尝试
//                httpURLConnection.setReadTimeout(8000);
//                httpURLConnection.setConnectTimeout(8000);
            InputStream inputStream = httpURLConnection.getInputStream();

            return BitmapUtils.decodeBitmapFromBytes(BitmapUtils.toByteArray(inputStream), width, height);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}