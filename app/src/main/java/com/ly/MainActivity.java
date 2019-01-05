package com.ly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ly.http.BitmapCallbackImpl;
import com.ly.http.HttpUtils;
import com.ly.http.utils.LogUtils;
import com.ly.nativead.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        umid=5bf36c99b465f5c4db0001d1&sdkv=1001&pkgName=xxx&channel=vivo&dataV=2&appV=1.0.1&json=xxx
//        HttpUtils.getInstance().get("https://idata.igame58.com/id/config_vivo.json")
//                .tag(this)
////        HttpUtils.getInstance().get("http://172.18.1.10:8080/lieyou/client/init")
////                .params("umid","5bf36c99b465f5c4db0001d1")
////                .params("sdkv","1001")
////                .params("pkgName","xxx")
////                .params("channel","vivo")
////                .params("dataV","2")
////                .params("appV","1.0.1")
////                .params("json","xxx")
//                .enqueue(new StringCallbackImpl() {
//
//                    @Override
//                    public void onSuccess(final String response) {
//
//                        String response_noEscape = response.replaceAll("\\\\", "");
//                        LogUtils.log("response", response_noEscape);
//
//                        try {
//                            JSONObject jsonObject=new JSONObject(response);
//                            JSONObject jobj=new JSONObject(jsonObject.getString("ad_datas"));
//
//                            LogUtils.log("adUrl",jobj.getString("adUrl"));
//
//                            JSONArray jsonArray=jobj.getJSONArray("push");
//                            JSONObject jobj_push= (JSONObject) jsonArray.get(0);
//                            LogUtils.log("fimage",jobj_push.getString("fimage"));
//                            LogUtils.log("pkgName",jobj_push.getString("pkgName"));
//                        } catch (JSONException e) {
//                            LogUtils.log(e.getMessage());
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//
//                        LogUtils.log("jsoncode", msg);
//
//
//                    }
//                });
//
//        HttpUtils.getInstance().get("http://img.zcool.cn/community/01b354556828fc0000012b20429d5e.jpg")
//                .tag(1)
//                .enqueue(new BitmapCallbackImpl(800, 800) {
//
//                    @Override
//                    public void onSuccess(Bitmap response) {
//
//                        ImageView iv = (ImageView) findViewById(R.id.iv);
//                        iv.setImageBitmap(response);
//                        LogUtils.log("图片宽度", response.getWidth());
//                    }
//
//                    @Override
//                    public void onLoding(final long current, final long length) {
//
//                        LogUtils.log(current, length);
//                        LogUtils.log("进度", current * 1f / length);
//
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//
//                        LogUtils.log("imgcodecode", msg);
//
//
//                    }
//                });
//        LogUtils.log("uiuoiokio");
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //权限未同意

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                LogUtils.log("aaaaaaaaaaaaaa");

                return;
            }

        }
//        File file = new File(getCacheDir().getPath() + "01b354556828fc0000012b20429d5e.jpg");
//        ImageView iv = (ImageView) findViewById(R.id.iv);
//        iv.setImageBitmap(BitmapUtils.decodeBitmapFromPath(file.getPath(),100,100));
        HttpUtils.getInstance().get("http://img.zcool.cn/community/01b354556828fc0000012b20429d5e.jpg")
                .tag(1)
                .enqueue(new BitmapCallbackImpl(getCacheDir().getPath() +"01b354556828fc0000012b20429d5e.jpg", 800, 800) {
                    @Override
                    public void onSuccess(Bitmap response) {
                        ImageView iv = (ImageView) findViewById(R.id.iv);
                        iv.setImageBitmap(response);
                        LogUtils.log("图片宽度", response.getWidth());
                    }

                    @Override
                    public void onLoding(long current, long length) {
                        LogUtils.log(current, length);
                        LogUtils.log("进度", current * 1f / length);
                    }

                    @Override
                    public void onFail( String errorMsg) {
                        LogUtils.log("imgcodecode", errorMsg);

                    }
                });


//        for (int i=0;i<1000;i++){
//            LogUtils.log(i);
//        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));

            }
        });
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

