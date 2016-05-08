package com.virtualchatrobot.utils;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Dolphix.J Qing on 2016/5/5.
 */
public class HttpUtil extends AsyncTask<String, Void, String>{

    private HttpClient httpClient;
    private HttpGet httpGet;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;
    private InputStream inputStream;
    private HttpGetListenerr httpGetListenerr;

    private String url;

    public HttpUtil(String url, HttpGetListenerr httpGetListenerr) {
        this.url = url;
        this.httpGetListenerr = httpGetListenerr;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            httpClient = new DefaultHttpClient();
            httpGet = new HttpGet(url);
            httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            StringBuffer stringBuffer = new StringBuffer();
            while(null != (line = bufferedReader.readLine())){
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        }catch (Exception e){e.printStackTrace();}

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        httpGetListenerr.getUrlData(s);
        super.onPostExecute(s);
    }


    public interface HttpGetListenerr{
        void getUrlData(String data);
    }
}
