package com.virtualchatrobot.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.virtualchatrobot.R;
import com.virtualchatrobot.utils.HttpUtil;
import com.virtualchatrobot.utils.HttpUtil.HttpGetListenerr;

/**
 * Created by Dolphix.J Qing on 2016/5/5.
 */
public class TulingActivity extends Activity implements HttpGetListenerr {

    private HttpUtil httpUtil;

    private static final String TAG = "Dolphix.J Qing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate: ");
        httpUtil = (HttpUtil) new HttpUtil("http://www.tuling123.com/openapi/api?key=b1a3d0aaa661812b28170f49919b834e&info=西安科技大学",this).execute();
    }

    @Override
    public void getUrlData(String data) {
        Log.i(TAG, "getUrlData: "+data);
        System.out.println(data);
    }
}
