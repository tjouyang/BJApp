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
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.hnust.bjapp.utils.DemoApplication;
import cn.edu.hnust.bjapp.utils.Utils;
import cn.edu.hnust.tbapp.R;


/**
 * Created by tjouyang on 2016/10/23.
 * 位置警报
 */

public class PositionActivity extends Activity {
    MapView mMapView;
    BaiduMap mBaiduMap;
    private LocationClient mLocClient;          //百度定位核心类
    private MyLocationListener myListener;
    SharedPreferences sp;
    ArrayList<LatLng> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
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
        ((TextView) findViewById(R.id.toolbar_title)).setText("位置警报");
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
        sp = getSharedPreferences("Config", MODE_PRIVATE);
        initMap();
        /*  在百度地图中画圆
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(hmPos)// 圆心
                .radius(300)// 半径 单位是米
                .fillColor(Color.RED);// 透明度 红 绿 蓝
//         .stroke(new Stroke(10, 0x600FF000));// 边框 参数1 线宽 参数2 颜色
        // 把覆盖物添加到地图中
        mBaiduMap.addOverlay(circleOptions);    */
    }

    private void initMap() {
        String path = Utils.getPath() + "/pigapp/LoadLocationInfoByUserIdServlet";
        JSONObject js = new JSONObject();
        try {
            js.put("sign", sp.getInt("sign", 0));
            js.put("id", sp.getInt("id", 0));
            js.put("type", 1);  //查询当前位置
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DemoApplication.getHttpRequestQueue().add(new JsonObjectRequest(path, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject guide_object = (JSONObject) jsonObject.get("guide");
                    LatLng guide_latLng = new LatLng(guide_object.getDouble("latitude"), guide_object.getDouble("longitude"));
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(guide_latLng)// 圆心
                            .radius(10)// 半径 单位是米
                            .fillColor(Color.GREEN);// 透明度 红 绿 蓝
//                            .stroke(new Stroke(10, 0x600FF000));// 边框 参数1 线宽 参数2 颜色
                    // 把覆盖物添加到地图中
                    mBaiduMap.addOverlay(circleOptions);
                    OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
                            .fontSize(24).fontColor(0xFFFF00FF).text(guide_object.getString("guide_nickname")).rotate(-30)
                            .position(guide_latLng);
                    mBaiduMap.addOverlay(ooText);
                    JSONArray tourist_array = jsonObject.getJSONArray("tourist");
                    for (int i = 0; i < tourist_array.length(); i++){
                        JSONObject tourist = (JSONObject) tourist_array.get(i);
                        LatLng tourist_latLng = new LatLng(tourist.getDouble("latitude"), tourist.getDouble("longitude"));
                        CircleOptions tourist_options = new CircleOptions();
                        tourist_options.center(tourist_latLng)// 圆心
                                .radius(10)// 半径 单位是米
                                .fillColor(Color.RED);// 透明度 红 绿 蓝
//                                .stroke(new Stroke(10, 0x600FF000));// 边框 参数1 线宽 参数2 颜色
                        // 把覆盖物添加到地图中
                        mBaiduMap.addOverlay(tourist_options);
                        OverlayOptions tourist_text = new TextOptions().bgColor(0xAAFFFF00)
                                .fontSize(24).fontColor(0xFFFF00FF).text(tourist.getString("tourist_nickname")).rotate(-30)
                                .position(tourist_latLng);
                        mBaiduMap.addOverlay(tourist_text);
                    }

//                    JSONArray array = jsonObject.getJSONArray("result");
//                    LatLng latLng;
//                    for(int i = 0; i < array.length(); i++){
//                        JSONObject tmp = (JSONObject) array.get(i);
//                        latLng = new LatLng(tmp.getDouble("latitude"), tmp.getDouble("longitude"));
//                        points.add(latLng);
//                    }
//                    if(points.size() <= 2){
//                        return;
//                    }
//                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
//                            .color(Color.BLUE).points(points);
//                    mBaiduMap.addOverlay(ooPolyline);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("PositionActivity", "Exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("PositionActivity", "Error");
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
