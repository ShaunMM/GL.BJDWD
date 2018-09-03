package com.bjdwd.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bjdwd.activitys.MainActivity;

/**
 * Created by dell on 2017/5/26.
 */

public class NetReceiver extends BroadcastReceiver {
    private static MainActivity mainActivitys;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获得网络连接服务
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.i("NetServer", "网络状态改变");
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // 获取WIFI连接状态
            NetworkInfo mobileNwtworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // 获取网络连接状态
            if ((wifiNetworkInfo != null && wifiNetworkInfo.isConnected())
                    || (mobileNwtworkInfo != null && mobileNwtworkInfo.isConnected())) {
                //获取 MainActivity
                //mainActivitys.keepAccessServer();
            }
        }

    }

    public static void setMainActivityClass(MainActivity mainActivity) {
        mainActivitys = mainActivity;
    }
}
