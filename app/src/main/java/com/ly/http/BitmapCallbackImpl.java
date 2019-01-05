package com.ly.http;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public abstract class BitmapCallbackImpl extends Callback<Bitmap> {

    private String cachePath;

    private int reqWidth, reqHeight;

    public BitmapCallbackImpl(int reqWidth, int reqHeight) {
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public BitmapCallbackImpl(String cachePath, int reqWidth, int reqHeight) {
        this.cachePath = cachePath;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public int getReqWidth() {
        return reqWidth;
    }

    public void setReqWidth(int reqWidth) {
        this.reqWidth = reqWidth;
    }

    public int getReqHeight() {
        return reqHeight;
    }

    public void setReqHeight(int reqHeight) {
        this.reqHeight = reqHeight;
    }
}
