package com.ly.http.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public void read2String(long contentLength, InputStream inputStream, IOListener ioListener) {

        if (!(inputStream instanceof BufferedInputStream)) {
            inputStream = new BufferedInputStream(inputStream);
        }
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream);

            StringBuilder sb = new StringBuilder();

            char[] buf = new char[1024];
            int len = 0;
            long current = 0;
            ioListener.onLoding(len, contentLength);

            while (isRunning && (len = reader.read(buf)) != -1) {
                sb.append(buf, 0, len);
                current += len;
                ioListener.onLoding(current, contentLength);
            }

            //中断
            if (len != -1) {
                if (ioListener != null) ioListener.onInterrupted();
            } else {

                if (ioListener != null) ioListener.onCompleted(sb.toString());

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ioListener.onFail("网络请求失败" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            ioListener.onFail("网络请求失败" + e.getMessage());

        } finally {
            close(reader);
            close(inputStream);
        }
    }


    public void read2File(String filePath, long contentLength, InputStream inputStream, IOListener ioListener) {
        File file = new File(filePath);
        if (file == null) {
            ioListener.onFail("文件创建失败，请检查路径是否合法以及读写权限");
            return;
        }
        if (file.length() > 0) {
            //有缓存
            ioListener.onCompleted(file);
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            try {
                int len = 0;
                long current = 0;

                ioListener.onLoding(len, contentLength);
                while (isRunning && (len = inputStream.read(buffer)) != -1) {

                    fileOutputStream.write(buffer, 0, len);
                    current += len;
                    ioListener.onLoding(current, contentLength);
                }

                fileOutputStream.flush();
                //中断
                if (len != -1) {
                    ioListener.onInterrupted();
                } else if (file.length() > 0) {

                    ioListener.onCompleted(file);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ioListener.onFail("文件创建失败，请检查路径是否合法以及读写权限" + e.getMessage());

        } finally {
            close(fileOutputStream);
            close(inputStream);
        }
    }

    public void read2ByteArray(long contentLength, InputStream inputStream, IOListener ioListener) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            int len = 0;
            long current = 0;

            ioListener.onLoding(len, contentLength);
            while (isRunning && (len = inputStream.read(buffer)) != -1) {

                byteArrayOutputStream.write(buffer, 0, len);
                current += len;
                ioListener.onLoding(current, contentLength);
            }

            byteArrayOutputStream.flush();
            //中断
            if (len != -1) {
                ioListener.onInterrupted();
            } else {
                ioListener.onCompleted(byteArrayOutputStream.toByteArray());

            }
        } catch (IOException e) {
            e.printStackTrace();
            ioListener.onFail("网络请求失败" + e.getMessage());

        } finally {
            close(byteArrayOutputStream);
            close(inputStream);
        }
    }
}
