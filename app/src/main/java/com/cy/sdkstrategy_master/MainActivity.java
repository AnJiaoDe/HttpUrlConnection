package com.cy.sdkstrategy_master;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cy.sdkstrategy_master.http.HttpUtils;
import com.cy.sdkstrategy_master.http.StringCallback;
import com.cy.sdkstrategy_master.utils.LogUtils;
import com.cy.sdkstrategy_master.utils.ToastUtils;

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
                .enqueue(new StringCallback() {

                    @Override
                    public void onSuccess(String response) {

                        ToastUtils.showToast(MainActivity.this,"陈宫");
                        LogUtils.log("main",response);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        ToastUtils.showToast(MainActivity.this,"失败");

                        LogUtils.log("main",msg);


                    }
                });


    }
}
