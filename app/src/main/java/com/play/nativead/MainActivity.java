package com.play.nativead;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cy.sdkstrategy_master.R;
import com.play.nativead.http.BitmapCallbackImpl;
import com.play.nativead.http.HttpUtils;
import com.play.nativead.http.StringCallbackImpl;
import com.play.nativead.http.utils.LogUtils;
import com.play.nativead.http.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        umid=5bf36c99b465f5c4db0001d1&sdkv=1001&pkgName=xxx&channel=vivo&dataV=2&appV=1.0.1&json=xxx
        HttpUtils.getInstance().get("https://idata.igame58.com/id/config_vivo.json")
//        HttpUtils.getInstance().get("http://172.18.1.10:8080/lieyou/client/init")
//                .params("umid","5bf36c99b465f5c4db0001d1")
//                .params("sdkv","1001")
//                .params("pkgName","xxx")
//                .params("channel","vivo")
//                .params("dataV","2")
//                .params("appV","1.0.1")
//                .params("json","xxx")
                .enqueue(new StringCallbackImpl() {

                    @Override
                    public void onSuccess(final String response) {

                        LogUtils.log("response", response);
                        String response_noEscape = response.replaceAll("\\\\", "");
                        String response_img = response_noEscape;
                        int indexStart = response_img.indexOf("fimage");
                        response_img = response_img.substring(indexStart, response_img.length());

                        response_img = response_img.substring(9, response_img.length());

                        response_img = response_img.substring(0, response_img.indexOf("\""));
                        LogUtils.log("response", response_img);

                        //???????????????????????????????????????????????


                        String response_url = response_noEscape;
                        indexStart = response_url.indexOf("adUrl");
                        response_url=response_url.substring(indexStart,response_url.length());
                        response_url=response_url.substring(8,response_url.length());
                        response_url=response_url.substring(0,response_url.indexOf("\""));

                        LogUtils.log("response", response_url);

                        ToastUtils.showToast(MainActivity.this, "陈宫");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtils.showToast(MainActivity.this, "失败");

                        LogUtils.log("main", msg);


                    }
                });
        HttpUtils.getInstance().get("http://img3.imgtn.bdimg.com/it/u=1779377288,3890256755&fm=26&gp=0.jpg")
//        HttpUtils.getInstance().get("http://172.18.1.10:8080/lieyou/client/init")
//                .params("umid","5bf36c99b465f5c4db0001d1")
//                .params("sdkv","1001")
//                .params("pkgName","xxx")
//                .params("channel","vivo")
//                .params("dataV","2")
//                .params("appV","1.0.1")
//                .params("json","xxx")
                .enqueue(new BitmapCallbackImpl(800, 800) {

                    @Override
                    public void onSuccess(Bitmap response) {

                        ImageView iv = (ImageView) findViewById(R.id.iv);
                        iv.setImageBitmap(response);
                        ToastUtils.showToast(MainActivity.this, "陈宫");
                        LogUtils.log("图片宽度", response.getWidth());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtils.showToast(MainActivity.this, "失败");

                        LogUtils.log("main", msg);


                    }
                });


    }
}
