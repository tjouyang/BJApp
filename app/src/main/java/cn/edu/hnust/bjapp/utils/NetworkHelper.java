package cn.edu.hnust.bjapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tjouyang on 2016/10/25.
 * 判断网络状态
 */

public class NetworkHelper {
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    /**
     * 获取网络状态
     * @param context
     * @return
     */
    public static int GetNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
            {
                netType = NETTYPE_CMNET;
            }
            else
            {
                netType = NETTYPE_CMWAP;
            }
        }
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 检查是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
