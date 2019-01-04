package com.ly.http;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public abstract class BitmapCallbackImpl extends Callback<Bitmap> {

    private File fileCache;

    private int reqWidth, reqHeight;

    public BitmapCallbackImpl(int reqWidth, int reqHeight) {
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public BitmapCallbackImpl(File fileCache, int reqWidth, int reqHeight) {
        this.fileCache = fileCache;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    public File getFileCache() {
        return fileCache;
    }

    public void setFileCache(File fileCache) {
        this.fileCache = fileCache;
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
