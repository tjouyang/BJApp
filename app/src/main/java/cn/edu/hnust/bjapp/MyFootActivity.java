package cn.edu.hnust.bjapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;

/**
 * Created by tjouyang on 2016/11/6.
 * 我的足迹
 */

public class MyFootActivity extends Activity{
    MapView mMapView;
    BaiduMap mBaiduMap;
    private LocationClient mLocClient;          //百度定位核心类
    private MyLocationListener myListener;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
        ((TextView) findViewById(R.id.toolbar_title)).setText("我的足迹");
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.removeViewAt(1);// 去掉百度logo图标
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));//设置缩放级别
        // 定位初始化
        mLocClient = new LocationClient(this);
        myListener = new MyLocationListener();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        sp  = getSharedPreferences("Config", MODE_PRIVATE);

        queryMyPosition();
    }

    /**
     * 查询我的足迹
     */

    private void queryMyPosition(){
        String path = Utils.getPath() + "/pigapp/LoadLocationInfoByUserIdServlet";
        JSONObject js = new JSONObject();
        try {
            js.put("sign", sp.getInt("sign", 0));
            js.put("id", sp.getInt("id", 0));
            js.put("type", 2);  //查询轨迹
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    ArrayList<LatLng> points = new ArrayList<>();
                    JSONArray array = jsonObject.getJSONArray("result");
                    LatLng latLng;
                    for(int i = 0; i < array.length(); i++){
                        JSONObject tmp = (JSONObject) array.get(i);
                        latLng = new LatLng(tmp.getDouble("latitude"), tmp.getDouble("longitude"));
                        points.add(latLng);
                    }
                    if(points.size() <= 2){
                        return;
                    }
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(Color.RED).points(points);
                    mBaiduMap.addOverlay(ooPolyline);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyFootActivity", "Exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("MyFootActivity", "Error");
            }
        }));
    }

    private class MyLocationListener implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocClient.stop();
        mLocClient.unRegisterLocationListener(myListener);
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
