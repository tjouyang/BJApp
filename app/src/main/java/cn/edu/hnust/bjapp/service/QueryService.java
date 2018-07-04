package cn.edu.hnust.bjapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.hnust.bjapp.cache.CacheUtils;
import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/31.
 *查询资料2
 */

public class QueryService extends Service {
    public class MyBinder extends Binder {
        public void callMethodInService(){
            methodInService();
        }
    }

    public void methodInService(){
        final SharedPreferences sp = getSharedPreferences("Config", MODE_PRIVATE);
        String path = Utils.getPath() + "/pigapp/PersonInformationServlet";
        JSONObject js = new JSONObject();
        try {
            js.put("sign", sp.getInt("sign", 2));
            js.put("id", sp.getInt("id", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Service", "onPersonInformationServlet");
        JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                CacheUtils.removeUserInfo();
                CacheUtils.putUserInfo(jsonObject);
                try {
                    sp.edit().putInt("auth", jsonObject.getInt("isauth")).apply();
                    Intent intent = new Intent();
                    intent.setAction("tj.ouyang.action");
                    intent.putExtra("isRefresh", true);
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //测试......
//                Log.e("QueryService", "test");
//                sp.edit().putInt("auth", 1).apply();
//                Intent intent = new Intent();
//                intent.setAction("tj.ouyang.action");
//                intent.putExtra("isRefresh", true);
//                sendBroadcast(intent);
            }
        });
        DemoApplication.getHttpRequestQueue().add(request);
        js = new JSONObject();
        //查询团名
        try {
            js.put("sign", sp.getInt("sign", 0));
            js.put("id", sp.getInt("id", -1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Service", "onLoadGroupInfoByIdServlet");
        path = Utils.getPath() + "/pigapp/LoadGroupInfoByIdServlet";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.length() == 0) {
                        //未加团
                        sp.edit().putBoolean("group", false).apply();
                    } else {
                        sp.edit().putBoolean("group", true).apply();
                        sp.edit().putInt("group_id", jsonObject.getInt("group_id")).apply();
                        sp.edit().putString("group_name", jsonObject.getString("group_name")).apply();
                        Intent intent = new Intent(QueryService.this, PositionService.class);
                        intent.putExtra("group_id", jsonObject.getInt("group_id"));
                        intent.putExtra("sign", sp.getInt("sign", 0));
                        intent.putExtra("id", sp.getInt("id", -1));
                        intent.putExtra("isRefresh", false);
                        intent.setAction("tj.ouyang.action");
                        startService(intent);
                        sendBroadcast(intent);
                        Log.e("Service", "sendBroadcastSuccess");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //测试
//                Intent intent = new Intent();
//                intent.putExtra("isRefresh", false);
//                intent.setAction("tj.ouyang.action");
//                sendBroadcast(intent);
            }
        });
        DemoApplication.getHttpRequestQueue().add(jsonObjectRequest);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
}
