package com.icecodersteam.kiria.postpigeon;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by disep on 12.06.16.
 */
public abstract class API extends AsyncTask<Void, Void, Void> {

    public static String response = null;
    static String token = null;
    List nameValuePairs = new ArrayList();

    public abstract void done();
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.print("Begin auth");
        done();
    }
    public static void SetToken(String t) {
        token = t;
    }
    public void SetParam(String key, String value){
        nameValuePairs.add(new BasicNameValuePair(key, value));
    }
    @Override
    protected Void doInBackground(Void... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost("http://postpigeon.handco.ru/pigeon.php?");
        nameValuePairs.add(new BasicNameValuePair("token", token));
        try {
            http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //http.setHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded;charset=UTF-8");
            response = URLDecoder.decode((String) httpclient.execute(http, new BasicResponseHandler()), "UTF-8");
            done();
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        System.out.print("Trying DONE METHOD call");
        done();
        /*JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(response);
            System.out.print("Answer decoded");
            if (dataJsonObj.has("access_token")) {
                ((Auth) c).AuthComplete(dataJsonObj.getString("access_token"));
            } else {
                Toast.makeText(c, dataJsonObj.getString("error_msg"), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }*/
    }
}
