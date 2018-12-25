package com.cy.sdkstrategy_master.http.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by cy on 2018/12/24.
 */

public class IOUtils {
    private boolean isRunning = true;

    public IOUtils() {
        isRunning = true;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public void read2String(InputStream in, String charset, IOListener ioListener) throws IOException {
        if (TextUtils.isEmpty(charset)) charset = "UTF-8";

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while (isRunning && (len = reader.read(buf)) != -1) {
            sb.append(buf, 0, len);
        }
        //中断
        if (reader.read(buf) != -1) {
            if (ioListener != null) ioListener.onInterrupted();
        } else {
            if (ioListener != null) ioListener.onCompleted(sb.toString());

        }
        reader.close();
        in.close();
    }

    public void read2ByteArray(InputStream input,IOListener ioListener) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1];
        int len = 0;
        while (isRunning && (len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        //中断
        if (input.read(buffer) != -1) {
            if (ioListener != null) ioListener.onInterrupted();
        } else {
            if (ioListener != null) ioListener.onCompleted(output.toByteArray());

        }
    }

}
