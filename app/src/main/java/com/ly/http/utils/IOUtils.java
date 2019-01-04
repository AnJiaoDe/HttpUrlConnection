package com.ly.http.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

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

    public void read2String(InputStream in, String charset, IOListener ioListener) {
        if (TextUtils.isEmpty(charset)) charset = "UTF-8";

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, charset);

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

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(reader);
            close(in);
        }
    }

    public void read2ByteArray(InputStream input, IOListener ioListener) {
        if (ioListener == null) return;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1];
        int len = 0;
        try {
            ioListener.onLoding(len,input.available());
            while (isRunning && (len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
                ioListener.onLoding(len,input.available());
            }

            //中断
            if (input.read(buffer) != -1) {
                ioListener.onInterrupted();
            } else {
                ioListener.onCompleted(output.toByteArray());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(output);
            close(input);
        }
    }

}
