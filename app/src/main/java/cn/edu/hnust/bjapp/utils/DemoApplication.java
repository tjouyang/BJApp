package cn.edu.hnust.bjapp.utils;

/**
 * Created by tjouyang on 2016/10/8.
 * application
 */


import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import cn.edu.hnust.bjapp.cache.ACache;


public class DemoApplication extends Application {
    private static RequestQueue mQueue;
    private static ACache aCache;
    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(this);
        aCache = ACache.get(this);
    }

    public static RequestQueue getHttpRequestQueue() {
        return mQueue;
    }


    public static ACache getACache() {
        return aCache;
    }
}
