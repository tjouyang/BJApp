package cn.edu.hnust.bjapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;

/**
 * Created by tjouyang on 2016/10/29.
 * x
 */

public class PositionService extends Service {
    Boolean isActive = true;
    BDLocationListener myListener;
    LocationClient mLocClient;
    int sign;
    int id;
    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        intent.setClass(this, PositionService.class);
        startService(intent);
        mLocClient.unRegisterLocationListener(myListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        int group_id = -1;
        try {
            group_id = intent.getIntExtra("group_id", -1);
            sign = intent.getIntExtra("sign", 2);
            id = intent.getIntExtra("id", -1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (group_id != -1) {
            Log.e("PositionService", "start");
            SDKInitializer.initialize(getApplicationContext());
            mLocClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
            );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            int span = 1000;   //1s钟汇报一次
            option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
            mLocClient.setLocOption(option);
            myListener = new BDLocationListener() {
                int i = 0;
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if(bdLocation == null){
                        return;
                    }
                    if(i == 0 || i == 180){
                        huibao(bdLocation);
                        i = 1;
                    }
                    i++;
                }
            };
            mLocClient.registerLocationListener(myListener);
            mLocClient.start();
        }
        if (group_id != -1) {
            JSONObject js = new JSONObject();
            try {
                js.put("group_id", group_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String path = Utils.getPath() + "/pigapp/QueryGroupStateServlet";
            JsonObjectRequest request = new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("state") == 2) {
//                            new AlertDialog.Builder(PositionService.this)
//                                    .setTitle("提示").setMessage("已开启分散活动").setPositiveButton("确定", null).create().show();
                            isActive = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            DemoApplication.getHttpRequestQueue().add(request);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void huibao(BDLocation bdLocation) {
        Log.e("PositionService", "Receive");
        JSONObject js = new JSONObject();
        try {
            js.put("sign", sign);
            js.put("id", id);
            js.put("latitude", bdLocation.getLatitude());
            js.put("longitude", bdLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path = Utils.getPath() + "/pigapp/ReportLocationServlet";
        DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
