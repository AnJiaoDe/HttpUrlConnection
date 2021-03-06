package com.ly;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ly.http.HttpUtils;
import com.ly.http.StringCallbackImpl;
import com.ly.http.utils.LogUtils;
import com.ly.nativead.R;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread myThread = new MyThread();
                myThread.start();
            }
        });
    }

    private static class MyThread extends Thread {

        @Override
        public void run() {
            HttpUtils.getInstance().get("https://raw.githubusercontent.com/AnJiaoDe/BaseDialog/master/app/build/outputs/apk/app-debug.apk")
                    .tag(this)
                    .block(new StringCallbackImpl() {

                        @Override
                        public void onSuccess(String response) {

                            LogUtils.log("responseAPK", response);

                        }

                        @Override
                        public void onLoding(long current, long length) {
                            LogUtils.log("进度",current*1f/length);

                        }

                        @Override
                        public void onFail( String msg) {

                            LogUtils.log("APKcode", msg);


                        }
                    });

            for (int i = 0; i < 1000; i++) {
                LogUtils.log(i);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpUtils.getInstance().cancelAll();
//                HttpUtils.getInstance().cancelByTag(1);

            }
        }, 0);
    }
}
