package com.ly.http;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public abstract class BitmapCallbackImpl extends Callback<Bitmap> {

    private int reqWidth, reqHeight;

    public BitmapCallbackImpl(int reqWidth, int reqHeight) {
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
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
